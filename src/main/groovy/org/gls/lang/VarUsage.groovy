package org.gls.lang

import groovy.util.logging.Slf4j
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotatedNode
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.*
import groovy.transform.TypeChecked
import org.eclipse.lsp4j.Location
import org.eclipse.lsp4j.Position
import org.eclipse.lsp4j.Range

@Slf4j
@TypeChecked
class VarUsage implements Reference {

    String sourceFileURI
    int columnNumber
    int lastColumnNumber
    int lineNumber
    int lastLineNumber

    String varName
    String typeName
    int definitionLineNumber
    Optional<String> declaringClass = Optional.empty()

    VarUsage(String sourceFileURI, ClassNode currentClass, ASTNode expression) {
        this.sourceFileURI = sourceFileURI
        initLocation(expression)

        if(expression instanceof ClassExpression) {
            initDeclarationReference(currentClass, expression as ClassExpression)
        } else if (expression instanceof VariableExpression) {
            initDeclarationReference(currentClass, expression as VariableExpression)
        }
    }

    VarUsage(String sourceFileURI, ClassNode currentClass, VariableExpression expression) {
        this.sourceFileURI = sourceFileURI
        initLocation(expression)
        initDeclarationReference(currentClass, expression)
    }

    void initLocation(ASTNode node) {
        this.columnNumber = node.columnNumber - 1
        this.lastColumnNumber = node.lastColumnNumber - 1
        this.lineNumber = node.lineNumber - 1
        this.lastLineNumber = node.lastLineNumber - 1
    }

    void initDeclarationReference(ClassNode currentClass, ClassExpression expression) {
        try {
            varName = expression.getType().getName()
            typeName = expression.getType().getName()
            declaringClass = Optional.of(typeName)
            definitionLineNumber = expression.getType().getLineNumber() - 1
        } catch (Exception e) {
            log.error("no var decl", e)
        }
    }

    void initDeclarationReference(ClassNode currentClass, VariableExpression expression) {
        try {
            typeName = expression.getType().getName()
            varName = expression.getName()
            if (expression.getAccessedVariable() != null) {
                def accessed = expression.getAccessedVariable()
                if(accessed instanceof AnnotatedNode) {
                    initAnnotatedNode(currentClass, accessed as AnnotatedNode)
                } else {
                    log.error " cast: ${expression}"
                }
            } else if(expression.isThisExpression()) {
                this.definitionLineNumber = expression.getType().getLineNumber() - 1
            } else if (expression.isSuperExpression() ) {
                this.definitionLineNumber = currentClass.getLineNumber()
            } else {
                log.error "No parentLineNumber: ${expression.getName()}"
                log.error "type: ${expression.getType()}"
                //TODO what then?
            }
        } catch (Exception e) {
            log.error("no var decl", e)
        }
    }

    void initAnnotatedNode(ClassNode currentClass, AnnotatedNode varDeclaration) {
        this.definitionLineNumber = varDeclaration.getLineNumber() - 1
        if ( varDeclaration.declaringClass != null ) {
            this.declaringClass = Optional.of(varDeclaration.declaringClass.getName())
        } else {
            // TODO not sure if this is correct.
            // Seems to be true for method arguments.
            this.declaringClass = Optional.of(currentClass.getName())
        }
    }

    Location getLocation() {
        Position start = new Position(lineNumber, columnNumber)
        Position end = new Position(lastLineNumber, lastColumnNumber)
        return new Location("file://" + sourceFileURI, new Range(start, end))
    }

    @Override
    public String toString() {
        return """VarUsage[
                sourceFileURI=$sourceFileURI,
                columnNumber=$columnNumber,
                lastColumnNumber=$lastColumnNumber,
                lineNumber=$lineNumber,
                lastLineNumber=$lastLineNumber,
                varName=$varName,
                typeName=$typeName,
                definitionLineNumber=$definitionLineNumber
                ]"""
    }

}
