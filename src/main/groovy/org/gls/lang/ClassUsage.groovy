package org.gls.lang

import groovy.util.logging.Slf4j
import org.codehaus.groovy.ast.ASTNode
import groovy.transform.TypeChecked
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.*

@Slf4j
@TypeChecked
class ClassUsage implements Reference {

    String sourceFileURI
    int columnNumber
    int lastColumnNumber
    int lineNumber
    int lastLineNumber
    String referencedClassName

    ClassUsage(String sourceFileURI, Parameter node) {
        this.sourceFileURI = sourceFileURI
        this.referencedClassName = node.getType().getName()
        initPosition(node)
    }

    ClassUsage(String sourceFileURI, FieldNode node) {
        this.sourceFileURI = sourceFileURI
        this.referencedClassName = node.getType().getName()
        initPosition(node)
    }

    ClassUsage(String sourceFileURI, DeclarationExpression expression) {
        this.sourceFileURI = sourceFileURI
        this.referencedClassName = expression.getLeftExpression().getType().getName()
        initPosition(expression)
    }

    ClassUsage(String sourceFileURI, MethodNode node) {
        this.sourceFileURI = sourceFileURI
        this.referencedClassName = node.getReturnType().getName()
        initPosition(node)
    }

    void initPosition(ASTNode node) {
        this.columnNumber = node.columnNumber - 1
        this.lastColumnNumber = node.lastColumnNumber - 1
        this.lineNumber = node.lineNumber - 1
        this.lastLineNumber = node.lastLineNumber - 1
    }

    String toString() {
        return """ClassUsage[
                sourceFileURI=$sourceFileURI,
                columnNumber=$columnNumber,
                lastColumnNumber=$lastColumnNumber,
                lineNumber=$lineNumber,
                referencedClassName=$referencedClassName,
                lastLineNumber=$lastLineNumber]"""
    }

}
