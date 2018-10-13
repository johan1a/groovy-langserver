package org.gls.lang.definition

import groovy.transform.ToString
import groovy.util.logging.Slf4j
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression
import org.gls.lang.ImmutableLocation
import org.gls.lang.LocationFinder
import org.gls.lang.ReferenceStorage
import org.gls.lang.reference.FuncReference
import org.gls.lang.types.ParameterExpression
import org.gls.lang.types.SimpleExpression
import org.gls.lang.types.Type

@ToString
@Slf4j
class FuncDefinition implements Definition<FuncDefinition, FuncReference> {

    ImmutableLocation location

    String returnType
    String functionName
    String definingClass
    SimpleExpression parameterTypes
    private Set<FuncReference> references

    FuncDefinition() {
    }

    FuncDefinition(String sourceFileURI, List<String> source, String definingClass, MethodNode node) {
        functionName = node.name
        returnType = node.getReturnType().name
        this.definingClass = definingClass
        parameterTypes = new ParameterExpression(parameters: node.parameters)
        this.location = LocationFinder.findLocation(sourceFileURI, source, node, functionName)
    }

    FuncDefinition(String sourceFileURI, String source, String definingClass, StaticMethodCallExpression expression) {
        functionName = expression.method
        returnType = expression.type.name
        this.definingClass = definingClass
        parameterTypes = new SimpleExpression(expression: expression.arguments)
        this.location = LocationFinder.findLocation(sourceFileURI, source, expression, functionName)
    }

    static FuncDefinition makeGetter(String sourceFileURI, List<String> source, String definingClass, FieldNode node) {
        FuncDefinition definition = new FuncDefinition()
        definition.functionName = "get" + node.name.substring(0, 1).toUpperCase() + node.name.substring(1)
        definition.returnType = node.type.name
        definition.definingClass = definingClass
        definition.parameterTypes = []
        definition.location = LocationFinder.findLocation(sourceFileURI, source, node, node.name)
        return definition
    }

    static FuncDefinition makeSetter(String sourceFileURI, List<String> source, String definingClass, FieldNode node) {
        FuncDefinition definition = new FuncDefinition()
        definition.functionName = "set" + node.name.substring(0, 1).toUpperCase() + node.name.substring(1)
        definition.returnType = "void"
        definition.definingClass = definingClass
        definition.parameterTypes = new SimpleExpression(name: node.type.name)
        definition.location = LocationFinder.findLocation(sourceFileURI, source, node, node.name)
        return definition
    }

    @Override
    void setReferences(Set<FuncReference> references) {
        this.references = references
    }

    @Override
    Set<FuncReference> getReferences() {
        return references
    }

    @Override
    Set<FuncReference> findMatchingReferences(ReferenceStorage storage, Set<FuncDefinition> definitions,
                                              Set<FuncReference> funcCalls) {
        funcCalls.findAll {
            it.definingClass == definingClass &&
                    it.functionName == functionName &&
                    sameArgumentTypesAs(storage, it)
        }
    }

    boolean sameArgumentTypesAs(ReferenceStorage storage, FuncReference funcReference) {
        List<Type> definitionParameterTypes = this.parameterTypes*.resolve(storage).types
        List<Type> referenceParameterTypes = funcReference.argumentTypes*.resolve(storage).types
        referenceParameterTypes == definitionParameterTypes
    }
}
