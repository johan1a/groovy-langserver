package org.gls.lang

import groovy.util.logging.Slf4j
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotatedNode
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.*
import groovy.transform.TypeChecked
import org.eclipse.lsp4j.Location
import org.eclipse.lsp4j.Position
import org.eclipse.lsp4j.Range

@Slf4j
@TypeChecked
class VarUsage implements Reference {

    String sourceFileURI
    int columnNumber
    int lastColumnNumber
    int lineNumber
    int lastLineNumber

    String varName
    String typeName
    int definitionLineNumber
    Optional<String> declaringClass = Optional.empty()

    VarUsage(String sourceFileURI, ClassNode currentClassNode, VariableExpression expression) {
        this.sourceFileURI = sourceFileURI
        this.columnNumber = expression.columnNumber - 1
        this.lastColumnNumber = expression.lastColumnNumber - 1
        this.lineNumber = expression.lineNumber - 1
        this.lastLineNumber = expression.lastLineNumber - 1

        varName = expression.getName()
        this.typeName = expression.getType().getName()
        try {

            if (expression.getAccessedVariable() != null) {
                AnnotatedNode varDeclaration = expression.getAccessedVariable() as AnnotatedNode
                this.definitionLineNumber = varDeclaration.getLineNumber() - 1
                if ( varDeclaration.declaringClass != null ) {
                    this.declaringClass = Optional.of(varDeclaration.declaringClass.getName())
                } else {
                    this.declaringClass = Optional.empty()
                }
            } else if(expression.isThisExpression()) {
                    this.definitionLineNumber = expression.getType().getLineNumber() - 1
            } else if (expression.isSuperExpression() ) {
                this.definitionLineNumber = currentClassNode.getLineNumber()
            } else {
                log.error "No parentLineNumber: ${expression.getName()}"
                log.error "type: ${expression.getType()}"
                //TODO what then?
            }
        } catch (Exception e) {
            log.error("no var decl", e)
        }
    }

    Location getLocation() {
        Position start = new Position(lineNumber, columnNumber)
        Position end = new Position(lastLineNumber, lastColumnNumber)
        return new Location("file://" + sourceFileURI, new Range(start, end))
    }

    @Override
    public String toString() {
        return """VarUsage[
                sourceFileURI=$sourceFileURI,
                columnNumber=$columnNumber,
                lastColumnNumber=$lastColumnNumber,
                lineNumber=$lineNumber,
                lastLineNumber=$lastLineNumber,
                varName=$varName,
                typeName=$typeName,
                definitionLineNumber=$definitionLineNumber
                ]"""
    }

}
