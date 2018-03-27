package org.gls.lang

/**
 * Created by joha on 27-03-2018.
 */
class ReferenceStorage {
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
}
