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

@Slf4j
class VarDefinition implements Definition {

    String sourceFileURI
    int columnNumber
    int lastColumnNumber
    int lineNumber
    int lastLineNumber
    String typeName
    String varName


    VarDefinition(String sourceFileURI, Parameter node) {
        this.sourceFileURI = sourceFileURI
        typeName = node.getType().getName()
        varName = node.getName()
        initPosition(node)
    }

    VarDefinition(String sourceFileURI, VariableExpression node) {
        this.sourceFileURI = sourceFileURI
        typeName = node.getType().getName()
        varName = node.getName()
        initPosition(node)
    }

    VarDefinition(String sourceFileURI, FieldNode node) {
        this.sourceFileURI = sourceFileURI
        typeName = node.getType().getName()
        varName = node.getName()
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
