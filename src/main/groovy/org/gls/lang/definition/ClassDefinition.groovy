package org.gls.lang.definition

import com.fasterxml.jackson.annotation.JsonIgnore
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

        type = new SimpleClass(name: node.name)
        this.location = LocationFinder.findLocation(sourceFileURI, source, node, type.nameWithoutPackage)
    }

    @Override
    void setReferences(Set<ClassReference> references) {
        this.references = references
    }

    @Override
    Set<ClassReference> getReferences() {
        return references
    }

    @Override
    Set<ClassReference> findMatchingReferences(ReferenceStorage storage, Set<ClassDefinition> classDefinitions,
                                               Set<ClassReference> references) {
        references.findAll {
            it.type.toString() == type.toString()
        }
    }

}
