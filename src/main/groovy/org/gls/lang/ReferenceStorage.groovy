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
    private Map<String, Set<VarDefinition> > varDefinitionsByFile = new HashMap<>()

    private Map<String, Set<FuncDefinition> > funcDefinitionsByFile = new HashMap<>()
    private Map<String, Set<FuncCall> > funcCallsByFile = new HashMap<>()


    // For finding var usages of a var definition
    private Map<VarDefinition, Set<VarUsage> > varUsagesByDefinition = new HashMap<>()

    Set<VarUsage> getVarUsagesByDefinition(VarDefinition varDefinition) {
        Set<VarUsage> usages = varUsagesByDefinition.get(varDefinition)
        if(usages == null) {
            usages = new HashSet<>()
            varUsagesByDefinition.put(varDefinition, usages)
        }
        return usages
    }

    void addVarUsageByDefinition(VarUsage varUsage, VarDefinition varDefinition) {
        getVarUsagesByDefinition(varDefinition).add(varUsage)
    }

    void addVarUsage(VarUsage usage) {
        Set<VarUsage> usages = varUsages.get(usage.sourceFileURI)
        if(usages == null) {
            usages = new HashSet<>()
            varUsages.put(usage.sourceFileURI, usages)
        }
        usages.add(usage)
    }

    Set<VarDefinition> getVarDefinitionsByFile(String filePath) {
        Set<VarDefinition> definitions = varDefinitionsByFile.get(filePath)
        if(definitions == null) {
            definitions = new HashSet<>()
            varDefinitionsByFile.put(filePath, definitions)
        }
        return definitions
    }

    Set<FuncDefinition> getFuncDefinitionsByFile(String filePath) {
        Set<FuncDefinition> definitions = funcDefinitionsByFile.get(filePath)
        if(definitions == null) {
            definitions = new HashSet<>()
            funcDefinitionsByFile.put(filePath, definitions)
        }
        return definitions
    }

    Set<FuncCall> getFuncCallsByFile(String filePath) {
        Set<FuncCall> calls = funcCallsByFile.get(filePath)
        if(calls == null) {
            calls = new HashSet<>()
            funcCallsByFile.put(filePath, calls)
        }
        return calls
    }

    void addVarDefinitionToFile(String filePath, VarDefinition varDefinition) {
        getVarDefinitionsByFile(filePath).add(varDefinition)
    }

    void addFuncDefinitionToFile(String filePath, FuncDefinition funcDefinition) {
        getFuncDefinitionsByFile(filePath).add(funcDefinition)
    }

    void addFuncCall(FuncCall call) {
        Set<FuncCall> calls = getFuncCallsByFile(call.getSourceFileURI())
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
}
