package org.gls.lang.definition

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.Variable
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.gls.exception.NotImplementedException
import org.gls.lang.ImmutableLocation
import org.gls.lang.LocationFinder
import org.gls.lang.ReferenceStorage
import org.gls.lang.reference.VarReference

@Slf4j
@TypeChecked
class VarDefinition implements Definition<VarDefinition, VarReference> {

    ImmutableLocation location

    String typeName
    String varName
    private Set<VarReference> references

    VarDefinition(String sourceFileURI, Expression node) {
        throw new NotImplementedException(sourceFileURI + node.toString())
    }

    VarDefinition(String sourceFileURI, List<String> source, Variable node) {
        throw new NotImplementedException(sourceFileURI + node.toString())
    }

    VarDefinition(String sourceFileURI, List<String> source, Parameter node) {
        typeName = node.type.name
        varName = node.name
        this.location = LocationFinder.findLocation(sourceFileURI, source, node, varName)
    }

    VarDefinition(String sourceFileURI, List<String> source, VariableExpression node) {
        typeName = node.type.name
        varName = node.name
        this.location = LocationFinder.findLocation(sourceFileURI, source, node, varName)
    }

    VarDefinition(String sourceFileURI, List<String> source, FieldNode node) {
        typeName = node.type.name
        varName = node.name
        this.location = LocationFinder.findLocation(sourceFileURI, source, node, varName)
    }

    @Override
    void setReferences(Set<VarReference> references) {
        this.references = references
    }

    @Override
    Set<VarReference> getReferences() {
        return references
    }

    @Override
    Set<VarReference> findMatchingReferences(ReferenceStorage storage, Set<VarDefinition> definitions,
                                             Set<VarReference> varUsages) {
        return varUsages.findAll {
            (it.sourceFileURI == sourceFileURI &&
                    it.typeName == typeName &&
                    it.definitionLineNumber == lineNumber &&
                    it.definitionStartColumn == location.range.start.character)
        }
    }

    @Override
    String toString() {
        return """VarDefinition[
                sourceFileURI=$sourceFileURI,
                columnNumber=$columnNumber,
                lastColumnNumber=$lastColumnNumber,
                lineNumber=$lineNumber,
                lastLineNumber=$lastLineNumber,
                varName=$varName,
                typeName=$typeName
                ]"""
    }

}
