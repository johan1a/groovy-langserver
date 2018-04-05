package org.gls.lang

import org.codehaus.groovy.ast.ASTNode
import org.eclipse.lsp4j.Location
import org.eclipse.lsp4j.Position
import org.eclipse.lsp4j.Range
import org.codehaus.groovy.ast.*

class FuncDefinition implements Definition {

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
        lastColumnNumber = node.lastColumnNumber - 1
        lineNumber = node.lineNumber - 1
        lastLineNumber = node.lastLineNumber - 1
    }

    String getSourceFileURI() {
        return sourceFileURI
    }

    Location getLocation() {
        Position start = new Position(lineNumber, columnNumber)
        Position end = new Position(lastLineNumber, lastColumnNumber)
        return new Location(getURI(), new Range(start, end))
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
