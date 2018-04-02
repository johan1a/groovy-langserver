package org.gls.lang

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.GroovyCodeVisitor
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.*
import org.codehaus.groovy.classgen.*
import org.codehaus.groovy.control.CompilationUnit
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.*
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
    String className

    FuncDefinition(String sourceFileURI, String className, MethodNode node) {
        this.sourceFileURI = sourceFileURI
        functionName = node.getName()
        returnType = node.getReturnType().getName()
        this.className = className
        initPosition(node)
    }

    private void initPosition(ASTNode node) {
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
        return """VarDefinition[
                sourceFileURI=$sourceFileURI,
                columnNumber=$columnNumber,
                lastColumnNumber=$lastColumnNumber,
                lineNumber=$lineNumber,
                lastLineNumber=$lastLineNumber,
                varName=$varName
                ]"""
    }
}
