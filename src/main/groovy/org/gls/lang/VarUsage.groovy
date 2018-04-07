package org.gls.lang

import groovy.util.logging.Slf4j
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotatedNode
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.*
import groovy.transform.TypeChecked

@Slf4j
@TypeChecked
class VarUsage implements HasLocation {

    String sourceFileURI
    int columnNumber
    int lastColumnNumber
    int lineNumber
    int lastLineNumber

    String varName
    String typeName
    int definitionLineNumber
    Optional<String> declaringClass = Optional.empty()

    VarUsage(String sourceFileURI, List<String> source, ClassNode currentClass, ASTNode expression) {
        this.sourceFileURI = sourceFileURI

        if(expression instanceof ClassExpression) {
            initDeclarationReference(currentClass, expression as ClassExpression)
        } else if (expression instanceof VariableExpression) {
            initDeclarationReference(currentClass, expression as VariableExpression)
        }
        initPosition(source, expression)
    }

    VarUsage(String sourceFileURI, List<String> source, ClassNode currentClass, VariableExpression expression) {
        this.sourceFileURI = sourceFileURI
        initDeclarationReference(currentClass, expression)
        initPosition(source, expression)
    }

    private void initPosition(List<String> source, ASTNode node) {
        lineNumber = node.lineNumber - 1
        lastLineNumber = node.lastLineNumber - 1
        if(lineNumber > 0 && varName != null){
            String firstLine = source[lineNumber]
            columnNumber = firstLine.indexOf(varName, node.columnNumber - 1)
            lastColumnNumber = columnNumber + varName.size() - 1
        } else {
            columnNumber = node.columnNumber
            lastColumnNumber = node.lastColumnNumber
        }
    }

    void initDeclarationReference(ClassNode currentClass, ClassExpression expression) {
        try {
            // TODO Maybe shouldn't be a varusage?
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
                } else if (accessed instanceof DynamicVariable) {
                    initDynamicVariable(currentClass, accessed as DynamicVariable)
                } else {
                    log.error " cast: ${expression}"
                    log.error " accessed: ${accessed}"
                }
            } else if(expression.isThisExpression()) {
                this.definitionLineNumber = expression.getType().getLineNumber() - 1
            } else if (expression.isSuperExpression() ) {
                ClassNode superClass = currentClass.getSuperClass()
                this.definitionLineNumber = superClass.getLineNumber() - superClass.getAnnotations().size()
            } else {
                log.error "No parentLineNumber: ${expression.getName()}"
                log.error "type: ${expression.getType()}"
                //TODO what then?
            }
        } catch (Exception e) {
            log.error("no var decl", e)
        }
    }

    void initDynamicVariable(ClassNode currentClass, DynamicVariable varDeclaration) {
        this.definitionLineNumber = varDeclaration.getType().getLineNumber() - 1
        this.declaringClass = Optional.of(currentClass.getName())
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
