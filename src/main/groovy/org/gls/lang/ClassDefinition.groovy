package org.gls.lang

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotatedNode
import org.codehaus.groovy.ast.GroovyCodeVisitor
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.*
import org.codehaus.groovy.classgen.*
import org.codehaus.groovy.control.CompilationUnit
import org.codehaus.groovy.ast.ClassNode
import groovy.transform.TypeChecked
import org.eclipse.lsp4j.Location

@Slf4j
@TypeChecked
class ClassDefinition implements HasLocation {

    ImmutableLocation location

    private String packageName
    private String className

    ClassDefinition(ClassNode node, String sourceFileURI, List<String> source) {
        className = node.getNameWithoutPackage()
        packageName = node.getPackageName()
        this.location = LocationFinder.findLocation(sourceFileURI, source, node, className)
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
