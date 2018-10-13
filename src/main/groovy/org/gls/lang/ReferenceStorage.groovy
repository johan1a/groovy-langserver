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

    Set<ClassDefinition> classDefinitions = [] as Set
    Set<ClassReference> classReferences = [] as Set
    Set<VarDefinition> varDefinitions = [] as Set
    Set<VarReference> varReferences = [] as Set
    Set<FuncDefinition> funcDefinitions = [] as Set
    Set<FuncReference> funcReferences = [] as Set

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

    ClassDefinition getClassDefinition(String fullClassName) {
        classDefinitions.find { it.type.toString() == fullClassName }
    }
}
