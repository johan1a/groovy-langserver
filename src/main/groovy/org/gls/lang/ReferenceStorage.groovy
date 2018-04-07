package org.gls.lang
import groovy.util.logging.Slf4j
import groovy.transform.TypeChecked

/**
 * Created by joha on 27-03-2018.
 */

@Slf4j
@TypeChecked
class ReferenceStorage {

    // Key is class functionName
    private Map<String, ClassDefinition> classDefinitions = new HashMap<>()

    // Key is soure file uri
    private Map<String, Set<ClassUsage> > classUsages = new HashMap<>()
    private Map<String, Set<VarUsage> > varUsages = new HashMap<>()
    private Set<VarDefinition> varDefinitions = new HashSet<>()

    private Set<FuncDefinition> funcDefinitions = new HashSet<>()
    private Set<FuncCall> funcCalls = new HashSet<>()

    void addVarUsage(VarUsage usage) {
        Set<VarUsage> usages = varUsages.get(usage.sourceFileURI)
        if(usages == null) {
            usages = new HashSet<>()
            varUsages.put(usage.sourceFileURI, usages)
        }
        usages.add(usage)
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
        Set<ClassUsage> references = getClassUsagesByFile(reference.sourceFileURI)
        references.add(reference)
    }

    Set<ClassUsage> getClassUsagesByFile(String filePath) {
        Set<ClassUsage> references = classUsages.get(filePath)
        if(references == null) {
            references = new HashSet<>()
            this.classUsages.put(filePath, references)
        }
        return  references
    }

    void addClassDefinitionToFile(String filePath, ClassDefinition definition) {
        classDefinitions.put(filePath, definition)
    }

    ClassDefinition getClassDefinitionByName(String className) {
        return classDefinitions.get(className)
    }

    Set<VarUsage> getVarUsagesByFile(String path) {
        return varUsages.get(path)
    }

    Set<VarUsage> getVarUsages() {
        return varUsages.values().flatten().toSet() as Set<VarUsage>
    }
}
