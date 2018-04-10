package org.gls.lang

import groovy.util.logging.Slf4j
import org.codehaus.groovy.ast.ASTNode
import groovy.transform.TypeChecked
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.*
import org.eclipse.lsp4j.Location

@Slf4j
@TypeChecked
class ClassReference implements Reference<ClassDefinition> {

    ImmutableLocation location

    String fullReferencedClassName

    String getShortReferencedClassName() {
        return fullReferencedClassName.split("\\.").last()
    }

    ClassReference(String sourceFileURI, List<String> source, Parameter node) {
        this.fullReferencedClassName = node.getType().getName()
        this.location = LocationFinder.findLocation(sourceFileURI, source, node, getShortReferencedClassName())
    }

    ClassReference(String sourceFileURI, List<String> source, FieldNode node) {
        this.fullReferencedClassName = node.getType().getName()
        this.location = LocationFinder.findLocation(sourceFileURI, source, node, getShortReferencedClassName())
    }

    ClassReference(String sourceFileURI, Expression expression) {
        throw new Exception()
    }

    ClassReference(String sourceFileURI, List<String> source, VariableExpression expression) {
        this.fullReferencedClassName = expression.getType().getName()
        this.location = LocationFinder.findLocation(sourceFileURI, source, expression, getShortReferencedClassName())
    }

    ClassReference(String sourceFileURI, List<String> source, DeclarationExpression expression) {
        this.fullReferencedClassName = expression.getLeftExpression().getType().getName()
        this.location = LocationFinder.findLocation(sourceFileURI, source, expression, getShortReferencedClassName())
    }

    ClassReference(String sourceFileURI, List<String> source, MethodNode node) {
        this.fullReferencedClassName = node.getReturnType().getName()
        this.location = LocationFinder.findLocation(sourceFileURI, source, node, getShortReferencedClassName())
    }

    ClassReference(String sourceFileURI, List<String> source, StaticMethodCallExpression expression) {
        this.fullReferencedClassName = expression.type.name
        this.location = LocationFinder.findLocation(sourceFileURI, source, expression, getShortReferencedClassName())
    }

    @Override
    Optional<ClassDefinition> findMatchingDefinition(Set<ClassDefinition> definitions) {
        Optional.ofNullable(definitions.find {
            it.getFullClassName() == getFullReferencedClassName()
        })
    }

    private static String simpleClassName(String name) {
        return name.split("\\.").last().trim()
    }

    String toString() {
        return """ClassReference[
                sourceFileURI=$sourceFileURI,
                columnNumber=$columnNumber,
                lastColumnNumber=$lastColumnNumber,
                lineNumber=$lineNumber,
                fullReferencedClassName=$fullReferencedClassName,
                lastLineNumber=$lastLineNumber]"""
    }

}
