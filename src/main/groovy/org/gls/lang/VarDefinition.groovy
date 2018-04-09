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
@TypeChecked
class VarDefinition implements Definition<VarUsage>  {

    ImmutableLocation location

    String typeName
    String varName

    VarDefinition(String sourceFileURI, List<String> source, Parameter node) {
        typeName = node.getType().getName()
        varName = node.getName()
        this.location = LocationFinder.findLocation(sourceFileURI, source, node, varName)
    }

    VarDefinition(String sourceFileURI, Expression node) {
        throw new Exception()
    }

    VarDefinition(String sourceFileURI, List<String> source, VariableExpression node) {
        typeName = node.getType().getName()
        varName = node.getName()
        this.location = LocationFinder.findLocation(sourceFileURI, source, node, varName)
    }

    VarDefinition(String sourceFileURI, List<String> source, FieldNode node) {
        typeName = node.getType().getName()
        varName = node.getName()
        this.location = LocationFinder.findLocation(sourceFileURI, source, node, varName)
    }

    @Override
    Set<VarUsage> findMatchingReferences(Set<VarUsage> varUsages) {
        return varUsages.findAll {
            it.getSourceFileURI() == getSourceFileURI() &&
                    it.typeName == typeName &&
                    it.definitionLineNumber == lineNumber
        }
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
