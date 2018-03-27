package org.gls.lang

/**
 * Created by joha on 27-03-2018.
 */
class ReferenceStorage {
    // For finding var usages of a var definition
    private Map<VarDefinition, Set<VarUsage> > varUsagesByDefinition = new HashMap<>()

    // For finding var usages of a class definition
    // TODO does it make sense?
    private Map<String, Set<VarUsage> > classVarUsages = new HashMap<>()

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
}
