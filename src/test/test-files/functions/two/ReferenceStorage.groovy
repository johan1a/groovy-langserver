package two

import groovy.util.logging.Slf4j
import groovy.transform.TypeChecked
import org.gls.lang.*

/**
 * Created by joha on 27-03-2018.
 */

@Slf4j
@TypeChecked
class ReferenceStorage {

    Set<org.gls.lang.definition.ClassDefinition> classDefinitions = new HashSet<>()
    Set<org.gls.lang.reference.ClassReference> classUsages = new HashSet<>()
    Set<org.gls.lang.definition.VarDefinition> varDefinitions = new HashSet<>()
    Set<org.gls.lang.reference.VarReference> varUsages = new HashSet<>()
    Set<org.gls.lang.definition.FuncDefinition> funcDefinitions = new HashSet<>()
    Set<FuncCall> funcCalls = new HashSet<>()

    void addVarUsage(org.gls.lang.reference.VarReference usage) {
        varUsages.add(usage)
    }

    void addVarDefinitionToFile(org.gls.lang.definition.VarDefinition varDefinition) {
        getVarDefinitions().add(varDefinition)
    }

    void addFuncDefinitionToFile(org.gls.lang.definition.FuncDefinition funcDefinition) {
        getFuncDefinitions().add(funcDefinition)
    }

    void addFuncCall(FuncCall call) {
        getFuncCalls().add(call)
    }

    void addClassUsage(org.gls.lang.reference.ClassReference reference) {
        getClassUsages().add(reference)
    }

    void addClassDefinitionToFile(org.gls.lang.definition.ClassDefinition definition) {
        classDefinitions.add(definition)
    }

}
