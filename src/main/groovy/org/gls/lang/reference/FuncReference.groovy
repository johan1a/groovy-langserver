package org.gls.lang.reference

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression
import org.codehaus.groovy.ast.expr.TupleExpression
import org.gls.lang.definition.FuncDefinition
import org.gls.lang.ImmutableLocation
import org.gls.lang.LocationFinder

@Slf4j
@TypeChecked
class FuncReference implements Reference<FuncDefinition> {

    ImmutableLocation location

    String functionName

    String definingClass
    List<String> argumentTypes
    private FuncDefinition definition

    FuncReference(String sourceFileURI, List<String> source, ClassNode currentClassNode,
                  StaticMethodCallExpression call) {
        functionName = call.methodAsString
        this.location = LocationFinder.findLocation(sourceFileURI, source, call, functionName)
        definingClass = currentClassNode.name
        initArguments(call.arguments)
    }

    FuncReference(String sourceFileURI, List<String> source, ClassNode currentClassNode, MethodCallExpression call,
                  VarReference receiver) {
        functionName = call.methodAsString
        this.location = LocationFinder.findLocation(sourceFileURI, source, call, functionName)
        initDefiningClass(currentClassNode, receiver)
        initArguments(call.arguments)
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

    void initArguments(Expression arguments) {
        log.error("initArguments: ${arguments.class}")
    }

    void initArguments(MethodCallExpression expression) {
        initArguments(expression.arguments)
    }

    void initArguments(ArgumentListExpression expression) {
        List<Expression> expressions = expression.expressions
        this.argumentTypes = expressions.collect { it.type.name }
    }

    void initArguments(ConstructorCallExpression expression) {
        Expression constructorArguments = expression.arguments
        log.debug("constructorArguments : ${constructorArguments}")
    }

    void initArguments(TupleExpression expression) {
        List<Expression> expressions = expression.expressions
        this.argumentTypes = expressions.collect { it.type.name }
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
    Optional<FuncDefinition> findMatchingDefinition(Set<FuncDefinition> definitions) {
        return Optional.ofNullable(definitions.find {
            it.definingClass == definingClass &&
                    it.functionName == functionName &&
                    it.parameterTypes == argumentTypes
        })
    }
}
