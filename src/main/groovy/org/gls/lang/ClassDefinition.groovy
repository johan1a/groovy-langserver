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

@Slf4j
@TypeChecked
class ClassDefinition {

    private String packageName
    private String className
    private String sourceFileURI

    private int columnNumber
    private int lastColumnNumber
    private int lineNumber
    private int lastLineNumber

    ClassDefinition(ClassNode node, String sourceFileURI) {
        columnNumber = node.getColumnNumber()
        lastColumnNumber = node.getLastColumnNumber()
        lineNumber = node.getLineNumber() + node.getAnnotations().size()
        lastLineNumber = node.getLastLineNumber()
        className = node.getNameWithoutPackage()
        packageName = node.getPackageName()
        this.sourceFileURI = sourceFileURI
    }

    String getFullClassName() {
        return packageName + "." + className
    }

    @Override
    public String toString() {
        return """ClassDefinition[
                packageName=$packageName,
                className=$className,
                sourceFileURI=$sourceFileURI,
                columnNumber=$columnNumber,
                lastColumnNumber=$lastColumnNumber,
                lineNumber=$lineNumber,
                lastLineNumber=$lastLineNumber]"""
    }

}
