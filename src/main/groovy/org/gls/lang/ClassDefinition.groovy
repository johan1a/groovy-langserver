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
class ClassDefinition implements HasLocation {

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

    ClassDefinition(ClassNode node, String sourceFileURI, List<String> source) {
        className = node.getNameWithoutPackage()
        packageName = node.getPackageName()
        initPosition(source, node)
        this.sourceFileURI = sourceFileURI
    }

    private void initPosition(List<String> source, ASTNode node) {
        lineNumber = node.lineNumber - 1
        lastLineNumber = node.lastLineNumber - 1
        if(lineNumber > 0 ){
            String firstLine = source[lineNumber]
            columnNumber = firstLine.indexOf(className, node.columnNumber - 1)
            lastColumnNumber = columnNumber + className.size() - 1
        } else {
            columnNumber = node.columnNumber
            lastColumnNumber = node.lastColumnNumber
        }
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
                definingClass=$className,
                sourceFileURI=$sourceFileURI,
                columnNumber=$columnNumber,
                lastColumnNumber=$lastColumnNumber,
                lineNumber=$lineNumber,
                lastLineNumber=$lastLineNumber]"""
    }

}
