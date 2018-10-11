package org.gls.lang.reference

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression
import org.gls.lang.ImmutableLocation
import org.gls.lang.LocationFinder
import org.gls.lang.ReferenceStorage
import org.gls.lang.definition.FuncDefinition
import org.gls.lang.types.SimpleExpression

@Slf4j
@TypeChecked
class FuncReference implements Reference<FuncDefinition> {

    ImmutableLocation location

    String functionName

    String definingClass
    SimpleExpression argumentTypes
    private FuncDefinition definition

    FuncReference(String sourceFileURI, List<String> source, ClassNode currentClassNode,
                  StaticMethodCallExpression call) {
        functionName = call.methodAsString
        this.location = LocationFinder.findLocation(sourceFileURI, source, call, functionName)
        definingClass = currentClassNode.name
        if (functionName == "toLocation") {
            log.debug("${functionName} call.arguments: ${call.arguments}")
        }

        this.argumentTypes = new SimpleExpression(containingClass: definingClass, expression: call.arguments)

        if (functionName == "toLocation") {
            log.debug("${functionName} arguments: ${argumentTypes}")
        }
    }

    FuncReference(String sourceFileURI, List<String> source, ClassNode currentClassNode, MethodCallExpression call,
                  VarReference receiver) {
        functionName = call.methodAsString
        this.location = LocationFinder.findLocation(sourceFileURI, source, call, functionName)
        initDefiningClass(currentClassNode, receiver)
        this.argumentTypes = new SimpleExpression(containingClass: definingClass, expression: call.arguments)
        log.debug("${functionName} arguments: ${argumentTypes}")
    }

    private void initDefiningClass(ClassNode currentClassNode, VarReference receiver) {
        if (receiver.varName == "this") {
            definingClass = currentClassNode.name
        } else if (receiver.varName == "super") {
            definingClass = currentClassNode.superClass.name
        } else {
            definingClass = receiver.typeName
        }
    }

    @Override
    void setDefinition(FuncDefinition definition) {
        this.definition = definition
    }

    @Override
    Optional<FuncDefinition> getDefinition() {
        return Optional.ofNullable(definition)
    }

    @Override
    Optional<FuncDefinition> findMatchingDefinition(ReferenceStorage storage, Set < FuncDefinition > definitions) {
        return Optional.ofNullable(definitions.find {
            it.definingClass == definingClass &&
                    it.functionName == functionName &&
                    it.sameArgumentTypesAs(storage, this)
        })
    }
}
