package org.gls.lang

import groovy.util.logging.Slf4j
import groovy.transform.TypeChecked
import org.gls.lang.definition.ClassDefinition
import org.gls.lang.definition.FuncDefinition
import org.gls.lang.definition.VarDefinition
import org.gls.lang.reference.ClassReference
import org.gls.lang.reference.FuncReference
import org.gls.lang.reference.VarReference

/**
 * Created by joha on 27-03-2018.
 */

@Slf4j
@TypeChecked
class ReferenceStorage {

    Set<ClassDefinition> classDefinitions = new HashSet<>()
    Set<ClassReference> classReferences = new HashSet<>()
    Set<VarDefinition> varDefinitions = new HashSet<>()
    Set<VarReference> varReferences = new HashSet<>()
    Set<FuncDefinition> funcDefinitions = new HashSet<>()
    Set<FuncReference> funcReferences = new HashSet<>()

    void addVarReference(VarReference reference) {
        varReferences.add(reference)
    }

    void addVarDefinitionToFile(VarDefinition varDefinition) {
        getVarDefinitions().add(varDefinition)
    }

    void addFuncDefinitionToFile(FuncDefinition funcDefinition) {
        getFuncDefinitions().add(funcDefinition)
    }

    void addFuncReference(FuncReference reference) {
        getFuncReferences().add(reference)
    }

    void addClassReference(ClassReference reference) {
        getClassReferences().add(reference)
    }

    void addClassDefinitionToFile(ClassDefinition definition) {
        classDefinitions.add(definition)
    }

    ClassDefinition getClassDefinition(String className) {
        classDefinitions.find { it.className == className }
    }
}
