package org.gls.lang.reference

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.codehaus.groovy.ast.ConstructorNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.expr.DeclarationExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.gls.lang.definition.ClassDefinition
import org.gls.lang.ImmutableLocation
import org.gls.lang.LocationFinder

@Slf4j
@TypeChecked
class ClassReference implements Reference<ClassDefinition> {

    ImmutableLocation location

    String fullReferencedClassName
    ClassDefinition definition

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

    ClassReference(String sourceFileURI, List<String> source, ConstructorNode node) {
        this.fullReferencedClassName = node.getDeclaringClass()
        this.location = LocationFinder.findLocation(sourceFileURI, source, node, getShortReferencedClassName())
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
    void setDefinition(ClassDefinition definition){
        this.definition = definition
    }

    @Override
    Optional<ClassDefinition> getDefinition() {
        return Optional.ofNullable(definition)
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
