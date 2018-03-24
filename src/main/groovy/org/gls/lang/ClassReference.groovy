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

@Slf4j
class ClassReference {

    private String packageName
    private String className
    private String sourceFileURI

    private int columnNumber
    private int lastColumnNumber
    private int lineNumber
    private int lastLineNumber

    String getFullClassName() {
        return packageName + "." + className
    }

}
