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
import org.codehaus.groovy.ast.*
import org.eclipse.lsp4j.Location
import org.eclipse.lsp4j.Position
import org.eclipse.lsp4j.Range
import org.codehaus.groovy.ast.*
import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j

@Slf4j
@TypeChecked
class FuncCall implements Reference {

    String sourceFileURI

    int columnNumber
    int lastColumnNumber
    int lineNumber
    int lastLineNumber

    String name
    VarUsage receiver

    int definitionLineNumber
    String definitionClass
    Optional<String> declaringClass = Optional.empty()

    FuncCall(String sourceFileURI, ClassNode currentClassNode, MethodCallExpression call, VarUsage receiver) {
        this.sourceFileURI = sourceFileURI
        this.receiver = receiver
        name = call.getMethodAsString()
        initPosition(call)
        definitionClass = receiver.typeName

        initArguments(call.getArguments())

        if(receiver.varName != "super") {
            log.info("-------")
            log.info("name: ${name}")
            log.info("definitionClass: ${definitionClass}")
        }
    }

    void initArguments(Expression arguments) {
        if (arguments instanceof ConstructorCallExpression) {
            ConstructorCallExpression constructorCallExpression = arguments as ConstructorCallExpression
            Expression constructorArguments = arguments.getArguments()
            log.info("constructorArguments : ${constructorArguments }")
        } else if (arguments instanceof ArgumentListExpression) {
            ArgumentListExpression argumentListExpression = arguments as ArgumentListExpression
            log.info("argumentListExpression: ${argumentListExpression}")
        }
    }

    private void initPosition(ASTNode node) {
        columnNumber = node.columnNumber - 1
        lastColumnNumber = node.lastColumnNumber - 1
        lineNumber = node.lineNumber - 1
        lastLineNumber = node.lastLineNumber - 1
    }
}
