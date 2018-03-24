package org.gls.groovy

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.GroovyCodeVisitor
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.*
import org.codehaus.groovy.classgen.*
import org.codehaus.groovy.control.CompilationUnit
import groovy.util.logging.Slf4j
import org.codehaus.groovy.ast.*


@Slf4j
@TypeChecked
class ClassVisitor implements GroovyClassVisitor {

    void visitClass(ClassNode node) {
        log.info "Visiting ClassNode $node"
        node.visitContents(this)
    }

    @Override
    void visitConstructor(ConstructorNode node){
        log.info "Visiting ConstructorNode $node"
    }

    @Override
    void visitField(FieldNode node){
        log.info "visiting FieldNode $node"
    }

    @Override
    void visitMethod(MethodNode node){
        log.info "visiting MethodNode $node"
    }

    @Override
    void visitProperty(PropertyNode node){
        log.info "visiting PropertyNode $node"
    }

}
