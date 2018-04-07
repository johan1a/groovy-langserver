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
class VarDefinition implements HasLocation {

    String sourceFileURI
    int columnNumber
    int lastColumnNumber
    int lineNumber
    int lastLineNumber
    String typeName
    String varName


    VarDefinition(String sourceFileURI, List<String> source, Parameter node) {
        this.sourceFileURI = sourceFileURI
        typeName = node.getType().getName()
        varName = node.getName()
        initPosition(source, node)
    }

    VarDefinition(String sourceFileURI, Expression node) {
        throw new Exception()
    }

    VarDefinition(String sourceFileURI, List<String> source, VariableExpression node) {
        this.sourceFileURI = sourceFileURI
        typeName = node.getType().getName()
        varName = node.getName()
        initPosition(source, node)
    }

    VarDefinition(String sourceFileURI, List<String> source, FieldNode node) {
        this.sourceFileURI = sourceFileURI
        typeName = node.getType().getName()
        varName = node.getName()
        initPosition(source, node)
    }

    private void initPosition(List<String> source, ASTNode node) {
        lineNumber = node.lineNumber - 1
        lastLineNumber = node.lastLineNumber - 1
        String firstLine = source[lineNumber]
        columnNumber = firstLine.indexOf(varName, node.columnNumber)
        lastColumnNumber = columnNumber + varName.size() - 1
    }


    String getSourceFileURI() {
        return sourceFileURI
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
