package org.gls.lang

import groovy.util.logging.Slf4j
import org.codehaus.groovy.ast.ASTNode
import groovy.transform.TypeChecked
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.*

@Slf4j
@TypeChecked
class ClassUsage implements HasLocation {

    String sourceFileURI
    int columnNumber
    int lastColumnNumber
    int lineNumber
    int lastLineNumber
    String fullReferencedClassName
    String referencedClassName

    ClassUsage(String sourceFileURI, List<String> source, Parameter node) {
        this.sourceFileURI = sourceFileURI
        this.fullReferencedClassName = node.getType().getName()
        referencedClassName = fullReferencedClassName.split("\\.").last()
        initPosition(node, source)
    }

    ClassUsage(String sourceFileURI, List<String> source, FieldNode node) {
        this.sourceFileURI = sourceFileURI
        this.fullReferencedClassName = node.getType().getName()
        initPosition(node, source)
    }

    ClassUsage(String sourceFileURI, Expression expression) {
       throw new Exception()
    }

    ClassUsage(String sourceFileURI, List<String> source, VariableExpression expression) {
        this.sourceFileURI = sourceFileURI
        this.fullReferencedClassName = expression.getType().getName()
        initPosition(expression, source)
    }

    ClassUsage(String sourceFileURI, List<String> source, DeclarationExpression expression) {
        this.sourceFileURI = sourceFileURI
        this.fullReferencedClassName = expression.getLeftExpression().getType().getName()
        initPosition(expression, source)
    }

    ClassUsage(String sourceFileURI, List<String> source, MethodNode node) {
        this.sourceFileURI = sourceFileURI
        this.fullReferencedClassName = node.getReturnType().getName()
        initPosition(node, source)
    }
    ClassUsage(String sourceFileURI, List<String> source, StaticMethodCallExpression expression ) {
        this.sourceFileURI = sourceFileURI
        this.fullReferencedClassName = expression.type.name
        initPosition(expression, source)
    }

    private void initPosition(ASTNode node, List<String> source) {
        lineNumber = node.lineNumber - 1
        lastLineNumber = node.lastLineNumber - 1
        if(lineNumber > 0 ){
            String firstLine = source[lineNumber]
            columnNumber = firstLine.indexOf(fullReferencedClassName, node.columnNumber - 1)
            lastColumnNumber = columnNumber + fullReferencedClassName.size() - 1
        } else {
            columnNumber = node.columnNumber
            lastColumnNumber = node.lastColumnNumber
        }
    }

    private static String simpleClassName(String name) {
        return name.split("\\.").last().trim()
    }

    String toString() {
        return """ClassUsage[
                sourceFileURI=$sourceFileURI,
                columnNumber=$columnNumber,
                lastColumnNumber=$lastColumnNumber,
                lineNumber=$lineNumber,
                fullReferencedClassName=$fullReferencedClassName,
                lastLineNumber=$lastLineNumber]"""
    }

}
