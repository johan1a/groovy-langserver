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

    Set<ClassDefinition> classDefinitions = new HashSet<>()
    Set<ClassReference> classUsages = new HashSet<>()
    Set<VarDefinition> varDefinitions = new HashSet<>()
    Set<VarReference> varUsages = new HashSet<>()
    Set<FuncDefinition> funcDefinitions = new HashSet<>()
    Set<FuncCall> funcCalls = new HashSet<>()

    void addVarUsage(VarReference usage) {
        varUsages.add(usage)
    }

    void addVarDefinitionToFile(VarDefinition varDefinition) {
        getVarDefinitions().add(varDefinition)
    }

    void addFuncDefinitionToFile(FuncDefinition funcDefinition) {
        getFuncDefinitions().add(funcDefinition)
    }

    void addFuncCall(FuncCall call) {
        getFuncCalls().add(call)
    }

    void addClassUsage(ClassReference reference) {
        getClassUsages().add(reference)
    }

    void addClassDefinitionToFile(ClassDefinition definition) {
        classDefinitions.add(definition)
    }

}
