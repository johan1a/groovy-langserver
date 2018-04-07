package org.gls.lang

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression

class FuncDefinition implements HasLocation {

    String sourceFileURI

    int columnNumber
    int lastColumnNumber
    int lineNumber
    int lastLineNumber

    String returnType
    String functionName
    String definingClass
    List<String> parameterTypes

    FuncDefinition(String sourceFileURI, List<String> source, String definingClass, MethodNode node) {
        this.sourceFileURI = sourceFileURI
        functionName = node.getName()
        returnType = node.getReturnType().getName()
        this.definingClass = definingClass
        initPosition(source, node)
        initParameterTypes(node.getParameters())
    }

    FuncDefinition(String sourceFileURI, String source, String definingClass, StaticMethodCallExpression expression) {
        this.sourceFileURI = sourceFileURI
        functionName = expression.getMethod()
        returnType = expression.type.getName()
        this.definingClass = definingClass
        initParameterTypes(expression.getArguments())
        initPosition(source, expression)
    }

    void initParameterTypes(Expression arguments) {
        throw new Exception()
    }
    void initParameterTypes(ArgumentListExpression arguments) {
        this.parameterTypes = arguments.collect() { it.getType().name }
    }

    void initParameterTypes(Parameter[] parameters) {
        this.parameterTypes = parameters.collect() { it.getType().name }
    }

    private void initPosition(List<String> source, ASTNode node) {
        lineNumber = node.lineNumber - 1
        lastLineNumber = node.lastLineNumber - 1
        if(lineNumber > 0 ){
            String firstLine = source[lineNumber]
            columnNumber = firstLine.indexOf(functionName, node.columnNumber - 1)
            lastColumnNumber = columnNumber + functionName.size() - 1
        } else {
            columnNumber = node.columnNumber
            lastColumnNumber = node.lastColumnNumber
        }
    }

    String getSourceFileURI() {
        return sourceFileURI
    }

    @Override
    public String toString() {
        return "FuncDefinition{" +
                "sourceFileURI='" + sourceFileURI + '\'' +
                ", columnNumber=" + columnNumber +
                ", lastColumnNumber=" + lastColumnNumber +
                ", lineNumber=" + lineNumber +
                ", lastLineNumber=" + lastLineNumber +
                ", returnType='" + returnType + '\'' +
                ", functionName='" + functionName + '\'' +
                ", definingClass='" + definingClass + '\'' +
                ", parameterTypes=" + parameterTypes +
                '}';
    }
}
