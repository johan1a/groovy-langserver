package org.gls.lang

import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.Parameter

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

    FuncDefinition(String sourceFileURI, String definingClass, MethodNode node) {
        this.sourceFileURI = sourceFileURI
        functionName = node.getName()
        returnType = node.getReturnType().getName()
        this.definingClass = definingClass
        initPosition(node)
        initParameterTypes(node.getParameters())
    }

    void initParameterTypes(Parameter[] parameters) {
        this.parameterTypes = parameters.collect() { it.getType().name }
    }

    private void initPosition(MethodNode node) {
        columnNumber = node.columnNumber - 1
        // TODO Hack because we don't know which is the last column number on the first line
        lastColumnNumber = 666
        lineNumber = node.lineNumber - 1
        lastLineNumber = node.lastLineNumber - 1
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
