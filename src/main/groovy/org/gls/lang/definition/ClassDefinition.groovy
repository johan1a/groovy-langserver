package org.gls.lang.definition

import groovy.transform.ToString
import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.codehaus.groovy.ast.ClassNode
import org.gls.lang.ImmutableLocation
import org.gls.lang.LocationFinder
import org.gls.lang.ReferenceStorage
import org.gls.lang.reference.ClassReference
import org.gls.lang.types.SimpleClass

@Slf4j
@TypeChecked
@ToString
class ClassDefinition implements Definition<ClassDefinition, ClassReference> {

    ImmutableLocation location

    private String packageName
    String className

    SimpleClass type

    private Set<ClassReference> references

    List<String> memberFunctions = []
    List<String> memberVariables = []

    ClassDefinition() {
    }

    @SuppressWarnings(["EmptyCatchBlock", "CatchException"])
    ClassDefinition(ClassNode node, String sourceFileURI, List<String> source) {
        try {
            memberFunctions = node.allDeclaredMethods*.name
            memberVariables = node.fields*.name
        } catch (Exception e1) {
        }

        className = node.nameWithoutPackage
        packageName = node.packageName
        String fullName = packageName ? "$packageName.$className" : className
        type = new SimpleClass(name: fullName)
        this.location = LocationFinder.findLocation(sourceFileURI, source, node, className)
    }

    @Override
    void setReferences(Set<ClassReference> references) {
        this.references = references
    }


    @Override
    Set<ClassReference> getReferences() {
        return references
    }

    String getFullClassName() {
        if (packageName != null) {
            return packageName + "." + className
        }
        return className
    }

    @Override
    Set<ClassReference> findMatchingReferences(ReferenceStorage storage, Set<ClassDefinition> classDefinitions,
                                               Set<ClassReference> references) {
        references.findAll {
            it.type.toString() == fullClassName
        }
    }

}
