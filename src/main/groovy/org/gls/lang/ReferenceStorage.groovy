package org.gls.lang
import groovy.util.logging.Slf4j
import groovy.transform.TypeChecked

/**
 * Created by joha on 27-03-2018.
 */

@Slf4j
@TypeChecked
class ReferenceStorage {

    Set<ClassDefinition> classDefinitions = new HashSet<>()
    Set<ClassReference> classUsages = new HashSet<>()
    Set<VarDefinition> varDefinitions = new HashSet<>()
    Set<VarReference> varUsages = new HashSet<>()
    Set<FuncDefinition> funcDefinitions = new HashSet<>()
    Set<FuncReference> funcReferences = new HashSet<>()

    void addVarUsage(VarReference usage) {
        varUsages.add(usage)
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

    void addClassUsage(ClassReference reference) {
        getClassUsages().add(reference)
    }

    void addClassDefinitionToFile(ClassDefinition definition) {
        classDefinitions.add(definition)
    }

}
