package org.gls.lang

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.*

@Slf4j
@TypeChecked
class VarDefinition implements Definition<VarReference>  {

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
    Set<VarReference> findMatchingReferences(Set<VarReference> varUsages) {
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
