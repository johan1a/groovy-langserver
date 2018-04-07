package two

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.ClassNode
import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.gls.lang.*
import org.gls.lang.VarUsage

@Slf4j
@TypeChecked
class FuncCall implements HasLocation {

    String sourceFileURI

    int columnNumber
    int lastColumnNumber
    int lineNumber
    int lastLineNumber

    String functionName

    String definingClass
    List<String> argumentTypes

    FuncCall(String sourceFileURI, ClassNode currentClassNode, StaticMethodCallExpression call) {
        this.sourceFileURI = sourceFileURI
        functionName = call.getMethodAsString()
        initPosition(call)
        definingClass = currentClassNode.getName()
        initArguments(call.getArguments())
    }

    FuncCall(String sourceFileURI, ClassNode currentClassNode, MethodCallExpression call, VarUsage receiver) {
        this.sourceFileURI = sourceFileURI
        functionName = call.getMethodAsString()
        initPosition(call)
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

    private void initPosition(ASTNode node) {
        columnNumber = node.columnNumber - 1
        lastColumnNumber = node.lastColumnNumber - 1
        lineNumber = node.lineNumber - 1
        lastLineNumber = node.lastLineNumber - 1
    }
}
