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
import groovy.transform.TypeChecked
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.*

@Slf4j
@TypeChecked
class ClassReference implements Reference {

    String sourceFileURI
    int columnNumber
    int lastColumnNumber
    int lineNumber
    int lastLineNumber
    String referencedClassName

    ClassReference(String sourceFileURI, FieldNode node) {
        this.sourceFileURI = sourceFileURI
        this.columnNumber = node.columnNumber - 1
        this.lastColumnNumber = node.lastColumnNumber - 1
        this.lineNumber = node.lineNumber - 1
        this.lastLineNumber = node.lastLineNumber - 1
        this.referencedClassName = node.getType().getName()
    }

    public String toString() {
        return """ClassReference[
                sourceFileURI=$sourceFileURI,
                columnNumber=$columnNumber,
                lastColumnNumber=$lastColumnNumber,
                lineNumber=$lineNumber,
                referencedClassName=$referencedClassName,
                lastLineNumber=$lastLineNumber]"""
    }

}
