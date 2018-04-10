package org.gls.lang

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.codehaus.groovy.ast.ClassNode

@Slf4j
@TypeChecked
class ClassDefinition implements Definition<ClassReference> {

    ImmutableLocation location

    private String packageName
    private String className
    private Set<ClassReference> references

    ClassDefinition(ClassNode node, String sourceFileURI, List<String> source) {
        className = node.getNameWithoutPackage()
        packageName = node.getPackageName()
        this.location = LocationFinder.findLocation(sourceFileURI, source, node, className)
    }

    @Override
    void setReferences(Set<ClassReference> references) {
        this.references = references
    }

    String getFullClassName() {
        if (packageName != null) {
            return packageName + "." + className
        }
        return className
    }

    @Override
    Set<ClassReference> findMatchingReferences(Set<ClassReference> references) {
        references.findAll {
            it.fullReferencedClassName == getFullClassName()
        }
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
