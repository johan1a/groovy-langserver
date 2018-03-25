package org.gls.lang

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.GroovyCodeVisitor
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.*
import org.codehaus.groovy.ast.*
import org.codehaus.groovy.classgen.*
import org.codehaus.groovy.control.CompilationUnit
import org.codehaus.groovy.ast.ClassNode
import groovy.transform.TypeChecked

@Slf4j
@TypeChecked
class VarReference implements Reference {

    String sourceFileURI
    int columnNumber
    int lastColumnNumber
    int lineNumber
    int lastLineNumber

    String varName
    String definitionClassName
    int definitionLineNumber

    VarReference(String sourceFileURI, ASTNode currentClassNode, VariableExpression expression) {
        this.sourceFileURI = sourceFileURI
        this.columnNumber = expression.columnNumber - 1
        this.lastColumnNumber = expression.lastColumnNumber - 1
        this.lineNumber = expression.lineNumber - 1
        this.lastLineNumber = expression.lastLineNumber - 1

        varName = expression.getName()
        this.definitionClassName = expression.getType().getName()
        log.info ""
        if(expression.isThisExpression()) {
            this.definitionLineNumber = expression.getType().getLineNumber() - 1
        } else if (expression.isSuperExpression() ) {
                this.definitionLineNumber = currentClassNode.getLineNumber()
        } else {
            ASTNode variable = expression.getAccessedVariable() as ASTNode
            log.info "variable1: ${variable}"
            log.info "lineNumber: ${variable.getLineNumber()}"
            log.info "columnNumber: ${variable.getColumnNumber()}"
            this.definitionLineNumber = variable.getLineNumber() - 1
        }
        log.info "varName: $varName, definitionLineNumber: $definitionLineNumber"
    }

    @Override
    public String toString() {
        return """VarReference[
                sourceFileURI=$sourceFileURI,
                columnNumber=$columnNumber,
                lastColumnNumber=$lastColumnNumber,
                lineNumber=$lineNumber,
                lastLineNumber=$lastLineNumber,
                varName=$varName,
                definitionClassName=$definitionClassName,
                definitionLineNumber=$definitionLineNumber
                ]"""
    }

}
