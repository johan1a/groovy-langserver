package org.gls.lang.reference

import groovy.transform.ToString
import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.codehaus.groovy.ast.ConstructorNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.GenericsType
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
import org.gls.lang.types.SimpleClass

@Slf4j
@TypeChecked
@ToString
class ClassReference implements Reference<ClassDefinition> {

    ImmutableLocation location

    SimpleClass type
    ClassDefinition definition

    String getShortReferencedClassName() {
        return type.name.split("\\.").last()
    }

    ClassReference(String sourceFileURI, List<String> source, Parameter parameter) {
        this.type = new SimpleClass(name: parameter.type.name)
        this.location = LocationFinder.findLocation(sourceFileURI, source, parameter, shortReferencedClassName)
    }

    ClassReference(String sourceFileURI, List<String> source, GenericsType genericsType) {
        this.type = new SimpleClass(name: genericsType.type.name)
        this.location = LocationFinder.findLocation(sourceFileURI, source, genericsType, shortReferencedClassName)
    }

    ClassReference(String sourceFileURI, List<String> source, FieldNode node) {
        List<SimpleClass> genericTypes = node.type.genericsTypes.collect { GenericsType type ->
            new SimpleClass(name: type.name)
        }
        this.type = new SimpleClass(name: node.type.name, genericTypes: genericTypes)
        this.location = LocationFinder.findLocation(sourceFileURI, source, node, shortReferencedClassName)
    }

    ClassReference(String sourceFileURI, List<String> source, Expression expression) {
        this.type = new SimpleClass(name: expression.type.name)
        this.location = LocationFinder.findLocation(sourceFileURI, source, expression, shortReferencedClassName)
    }

    ClassReference(String sourceFileURI, List<String> source, VariableExpression expression) {
        this.type = new SimpleClass(name: expression.type.name)
        this.location = LocationFinder.findLocation(sourceFileURI, source, expression, shortReferencedClassName)
    }

    ClassReference(String sourceFileURI, List<String> source, DeclarationExpression expression) {
        this.type = new SimpleClass(name: expression.leftExpression.type.name)
        this.location = LocationFinder.findLocation(sourceFileURI, source, expression, shortReferencedClassName)
    }

    ClassReference(String sourceFileURI, List<String> source, ConstructorNode node) {
        this.type = new SimpleClass(name: node.declaringClass.name)
        this.location = LocationFinder.findLocation(sourceFileURI, source, node, shortReferencedClassName)
    }

    ClassReference(String sourceFileURI, List<String> source, MethodNode node) {
        this.type = new SimpleClass(name: node.returnType.name)
        this.location = LocationFinder.findLocation(sourceFileURI, source, node, shortReferencedClassName)
    }

    ClassReference(String sourceFileURI, List<String> source, StaticMethodCallExpression expression) {
        this.type = new SimpleClass(name: expression.type.name)
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
            it.type == type
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
        if (type != that.type) {
            return false
        }

        return location == that.location
    }

    @SuppressWarnings(["DuplicateNumberLiteral"])
    int hashCode() {
        int result
        result = (location != null ? location.hashCode() : 0)
        result = 31 * result + (type != null ? type.hashCode() : 0)
        result = 31 * result + (definition != null ? definition.hashCode() : 0)
        return result
    }
}
