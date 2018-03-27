package org.gls.lang

/**
 * Created by joha on 27-03-2018.
 */
class ReferenceStorage {
    // For finding var usages of a var definition
    private Map<VarDefinition, Set<VarUsage> > varUsagesByDefinition = new HashMap<>()

    private Map<String, Set<VarUsage> > varUsages = new HashMap<>()
    private Map<String, Set<VarDefinition> > varDefinitionsByFile = new HashMap<>()

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

    void addVarDefinitionToFile(String filePath, VarDefinition varDefinition) {
        getVarDefinitionsByFile(filePath).add(varDefinition)
    }
}
