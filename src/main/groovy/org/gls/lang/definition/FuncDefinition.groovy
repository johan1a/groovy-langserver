package org.gls.lang.definition

import groovy.transform.ToString
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression
import org.gls.exception.NotImplementedException
import org.gls.lang.ImmutableLocation
import org.gls.lang.LocationFinder
import org.gls.lang.reference.FuncReference

@ToString
class FuncDefinition implements Definition<FuncReference> {

    ImmutableLocation location

    String returnType
    String functionName
    String definingClass
    List<String> parameterTypes
    private Set<FuncReference> references

    FuncDefinition() {
    }

    FuncDefinition(String sourceFileURI, List<String> source, String definingClass, MethodNode node) {
        functionName = node.name
        returnType = node.getReturnType().name
        this.definingClass = definingClass
        initParameterTypes(node.parameters)
        this.location = LocationFinder.findLocation(sourceFileURI, source, node, functionName)
    }

    FuncDefinition(String sourceFileURI, String source, String definingClass, StaticMethodCallExpression expression) {
        functionName = expression.method
        returnType = expression.type.name
        this.definingClass = definingClass
        initParameterTypes(expression.arguments)
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
        definition.parameterTypes = [node.type.name]
        definition.location = LocationFinder.findLocation(sourceFileURI, source, node, node.name)
        return definition
    }

    void initParameterTypes(Expression arguments) {
        throw new NotImplementedException(arguments.toString())
    }

    void initParameterTypes(ArgumentListExpression arguments) {
        this.parameterTypes = arguments.collect { it.type.name }
    }

    void initParameterTypes(Parameter[] parameters) {
        this.parameterTypes = parameters.collect { it.type.name }
    }

    @Override
    void setReferences(Set<FuncReference> references) {
        this.references = references
    }

    @Override
    void setName(String name) {
        this.functionName = name
    }

    @Override
    Set<FuncReference> getReferences() {
        return references
    }

    @Override
    Set<FuncReference> findMatchingReferences(Set<FuncReference> funcCalls) {
        funcCalls.findAll {
            it.definingClass == definingClass &&
                    it.functionName == functionName &&
                    it.argumentTypes == this.parameterTypes
        }
    }

}
