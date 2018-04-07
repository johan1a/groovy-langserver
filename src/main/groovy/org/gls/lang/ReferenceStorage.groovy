package org.gls.lang
import groovy.util.logging.Slf4j
import groovy.transform.TypeChecked

/**
 * Created by joha on 27-03-2018.
 */

@Slf4j
@TypeChecked
class ReferenceStorage {

    private Set<ClassDefinition> classDefinitions = new HashSet<>()
    private Set<ClassUsage> classUsages = new HashSet<>()
    private Set<VarUsage> varUsages = new HashSet<>()
    private Set<VarDefinition> varDefinitions = new HashSet<>()
    private Set<FuncDefinition> funcDefinitions = new HashSet<>()
    private Set<FuncCall> funcCalls = new HashSet<>()

    void addVarUsage(VarUsage usage) {
        varUsages.add(usage)
    }

    Set<VarDefinition> getVarDefinitions() {
        return varDefinitions
    }

    Set<FuncDefinition> getFuncDefinitions() {
        return funcDefinitions
    }

    Set<FuncCall> getFuncCalls() {
        return funcCalls
    }

    void addVarDefinitionToFile(VarDefinition varDefinition) {
        getVarDefinitions().add(varDefinition)
    }

    void addFuncDefinitionToFile(FuncDefinition funcDefinition) {
        getFuncDefinitions().add(funcDefinition)
    }

    void addFuncCall(FuncCall call) {
        Set<FuncCall> calls = getFuncCalls()
        calls.add(call)
    }

    void addClassUsage(ClassUsage reference) {
        getClassUsages().add(reference)
    }

    Set<ClassUsage> getClassUsages() {
        return classUsages
    }

    void addClassDefinitionToFile(ClassDefinition definition) {
        classDefinitions.add(definition)
    }

    Set<ClassDefinition> getClassDefinitions() {
        return classDefinitions
    }

    Set<VarUsage> getVarUsages() {
        return varUsages
    }
}
