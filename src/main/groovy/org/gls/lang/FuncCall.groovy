package org.gls.lang

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.ClassNode
import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.eclipse.lsp4j.Location

@Slf4j
@TypeChecked
class FuncCall implements HasLocation {

    ImmutableLocation location

    String functionName

    String definingClass
    List<String> argumentTypes

    FuncCall(String sourceFileURI, List<String> source, ClassNode currentClassNode, StaticMethodCallExpression call) {
        functionName = call.getMethodAsString()
        this.location = LocationFinder.findLocation(sourceFileURI, source, call, functionName)
        definingClass = currentClassNode.getName()
        initArguments(call.getArguments())
    }

    FuncCall(String sourceFileURI, List<String> source, ClassNode currentClassNode, MethodCallExpression call, VarUsage receiver) {
        functionName = call.getMethodAsString()
        this.location = LocationFinder.findLocation(sourceFileURI, source, call, functionName)
        initDefiningClass(currentClassNode, receiver)
        initArguments(call.getArguments())
    }

    private void initDefiningClass(ClassNode currentClassNode, VarUsage receiver) {
        if(receiver.varName == "this"){
            definingClass = currentClassNode.getName()
        } else if (receiver.varName == "super") {
            definingClass = currentClassNode.getSuperClass().getName()
        } else {
            definingClass = receiver.typeName
        }
    }

    void initArguments(Expression arguments) {
        if (arguments instanceof ConstructorCallExpression) {
            initArguments(arguments as ConstructorCallExpression)
        } else if (arguments instanceof ArgumentListExpression) {
            initArguments(arguments as ArgumentListExpression)
        } else if (arguments instanceof TupleExpression) {
            initArguments(arguments as TupleExpression)
        } else {
            log.error("instanceof: ${arguments}", arguments)
        }
    }

    void initArguments(ArgumentListExpression expression) {
        List<Expression> expressions = expression.getExpressions()
        this.argumentTypes = expressions.collect{ it.getType().getName() }
    }

    void initArguments(ConstructorCallExpression expression) {
        Expression constructorArguments = expression.getArguments()
        log.info("constructorArguments : ${constructorArguments }")
    }

    void initArguments(TupleExpression expression) {
        List<Expression> expressions = expression.expressions
        this.argumentTypes = expressions.collect{ it.getType().getName() }
    }
}
