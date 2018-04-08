package org.gls.lang

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression
import org.eclipse.lsp4j.Location

class FuncDefinition implements HasLocation {

    Location location
    int getLineNumber() { return location.getRange().start.line}
    int getLastLineNumber() {return location.getRange().end.line}
    int getColumnNumber() {return location.getRange().start.character}
    int getLastColumnNumber() {return location.getRange().end.character}
    String getSourceFileURI() { return location.uri }

    String returnType
    String functionName
    String definingClass
    List<String> parameterTypes

    FuncDefinition(String sourceFileURI, List<String> source, String definingClass, MethodNode node) {
        functionName = node.getName()
        returnType = node.getReturnType().getName()
        this.definingClass = definingClass
        initParameterTypes(node.getParameters())
        this.location = LocationFinder.findLocation(sourceFileURI, source, node, functionName)
    }

    FuncDefinition(String sourceFileURI, String source, String definingClass, StaticMethodCallExpression expression) {
        functionName = expression.getMethod()
        returnType = expression.type.getName()
        this.definingClass = definingClass
        initParameterTypes(expression.getArguments())
        this.location = LocationFinder.findLocation(sourceFileURI, source, expression, functionName)
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
