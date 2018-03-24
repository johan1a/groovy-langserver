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
import java.util.Map
import groovy.transform.TypeChecked

@Slf4j
@TypeChecked
class ReferenceStorage {

    private Map<String, ClassDefinition> classDefinitions = new HashMap<>()
    private Map<String, Set<ClassReference> > classReferences

    void addClassDefinition(ClassDefinition definition) {
        classDefinitions.put(definition.getFullClassName(), definition)
        log.info "Added $definition"
    }

    void addClassReference(ClassReference reference) {
        Set<ClassReference> references = classReferences.get(reference.getFullClassName())
        if(references == null) {
            references = new HashSet<>()
            classReferences.put(reference.getFullClassName(), references)
        }
        references.add(reference)
    }

    // Fully qualified class name
    ClassDefinition getClassDefinition(String fullClassName) {
        return classDefinitions.get(fullClassName)
    }

    // Fully qualified class name
    Set<ClassReference> getClassReferences(String fullClassName) {
        return classReferences.get(fullClassName)
    }

}
