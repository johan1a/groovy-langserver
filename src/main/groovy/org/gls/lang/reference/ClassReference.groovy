package org.gls.lang.reference

import groovy.transform.ToString
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
import org.gls.lang.ReferenceStorage
import org.gls.lang.definition.ClassDefinition
import org.gls.lang.ImmutableLocation
import org.gls.lang.LocationFinder

@Slf4j
@TypeChecked
@ToString
class ClassReference implements Reference<ClassDefinition> {

    ImmutableLocation location

    String fullReferencedClassName
    ClassDefinition definition

    String getShortReferencedClassName() {
        return fullReferencedClassName.split("\\.").last()
    }

    ClassReference(String sourceFileURI, List<String> source, Parameter parameter) {
        this.fullReferencedClassName = parameter.type.name
        this.location = LocationFinder.findLocation(sourceFileURI, source, parameter, shortReferencedClassName)
    }

    ClassReference(String sourceFileURI, List<String> source, FieldNode node) {
        this.fullReferencedClassName = node.type.name
        this.location = LocationFinder.findLocation(sourceFileURI, source, node, shortReferencedClassName)
    }

    ClassReference(String sourceFileURI, List<String> source, Expression expression) {
        this.fullReferencedClassName = expression.type.name
        this.location = LocationFinder.findLocation(sourceFileURI, source, expression, shortReferencedClassName)
    }

    ClassReference(String sourceFileURI, List<String> source, VariableExpression expression) {
        this.fullReferencedClassName = expression.type.name
        this.location = LocationFinder.findLocation(sourceFileURI, source, expression, shortReferencedClassName)
    }

    ClassReference(String sourceFileURI, List<String> source, DeclarationExpression expression) {
        this.fullReferencedClassName = expression.leftExpression.type.name
        this.location = LocationFinder.findLocation(sourceFileURI, source, expression, shortReferencedClassName)
    }

    ClassReference(String sourceFileURI, List<String> source, ConstructorNode node) {
        this.fullReferencedClassName = node.declaringClass
        this.location = LocationFinder.findLocation(sourceFileURI, source, node, shortReferencedClassName)
    }

    ClassReference(String sourceFileURI, List<String> source, MethodNode node) {
        this.fullReferencedClassName = node.returnType.name
        this.location = LocationFinder.findLocation(sourceFileURI, source, node, shortReferencedClassName)
    }

    ClassReference(String sourceFileURI, List<String> source, StaticMethodCallExpression expression) {
        this.fullReferencedClassName = expression.type.name
        this.location = LocationFinder.findLocation(sourceFileURI, source, expression, shortReferencedClassName)
    }

    @Override
    void setDefinition(ClassDefinition definition) {
        this.definition = definition
    }

    @Override
    Optional<ClassDefinition> getDefinition() {
        return Optional.ofNullable(definition)
    }

    @Override
    Optional<ClassDefinition> findMatchingDefinition(ReferenceStorage storage, Set<ClassDefinition> definitions) {
        Optional.ofNullable(definitions.find {
            it.fullClassName == fullReferencedClassName
        })
    }

    boolean equals(Object o) {
        if (this.is(o)) {
            return true
        }
        if (getClass() != o.class) {
            return false
        }

        ClassReference that = (ClassReference) o

        if (getDefinition() != that.getDefinition()) {
            return false
        }
        if (fullReferencedClassName != that.fullReferencedClassName) {
            return false
        }

        return location == that.location
    }

    @SuppressWarnings(["DuplicateNumberLiteral"])
    int hashCode() {
        int result
        result = (location != null ? location.hashCode() : 0)
        result = 31 * result + (fullReferencedClassName != null ? fullReferencedClassName.hashCode() : 0)
        result = 31 * result + (definition != null ? definition.hashCode() : 0)
        return result
    }
}
