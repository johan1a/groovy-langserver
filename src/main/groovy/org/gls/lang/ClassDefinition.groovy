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
class ClassDefinition implements Definition {

    private String packageName
    private String className
    private String sourceFileURI

    int columnNumber
    int lastColumnNumber
    int lineNumber
    int lastLineNumber

    String getSourceFileURI() {
        return sourceFileURI
    }

    ClassDefinition(ClassNode node, String sourceFileURI) {
        columnNumber = node.getColumnNumber() - 1
        lastColumnNumber = node.getLastColumnNumber() - 1
        lineNumber = node.getLineNumber() + node.getAnnotations().size() - 1
        lastLineNumber = node.getLastLineNumber() - 1
        className = node.getNameWithoutPackage()
        packageName = node.getPackageName()
        this.sourceFileURI = sourceFileURI
    }

    String getFullClassName() {
        if(packageName != null) {
            return packageName + "." + className
        }
        return className
    }

    @Override
    String toString() {
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
