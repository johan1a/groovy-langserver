

import groovy.util.logging.Slf4j
import org.gls.lang.*
import org.eclipse.lsp4j.Location
import org.eclipse.lsp4j.Position
import org.eclipse.lsp4j.Range
import org.eclipse.lsp4j.*

@Slf4j
class ReferenceFinder {

    ReferenceStorage storage = new ReferenceStorage()

    Set<ClassUsage> getClassUsages(String fileUri) {
        return storage.getClassUsages()
    }

    void addClassDefinition(ClassDefinition definition) {
        storage.addClassDefinitionToFile(definition)
    }

    void addClassUsage(ClassUsage reference) {
        storage.addClassUsage(reference)
    }

    void addVarUsage(VarUsage usage) {
        storage.addVarUsage(usage)
    }

    void addFuncDefinition(FuncDefinition funcDefinition) {
        storage.addFuncDefinitionToFile(funcDefinition)
    }

    void addFuncCall(FuncCall funcCall) {
        storage.addFuncCall(funcCall)
    }

    void addVarDefinition(VarDefinition definition) {
        storage.addVarDefinitionToFile(definition)
    }

    List<Location> getDefinition(TextDocumentPositionParams params) {
        List<Location> varDefinitions = getVarDefinition(params)
        if(!varDefinitions.isEmpty()) {
            return varDefinitions
        }
        List<Location> classDefinitions = getClassDefinition(params)
        if (!classDefinitions.isEmpty()){
            return classDefinitions
        }
        return getFuncDefinition(params)
    }

    List<Location> getReferences(ReferenceParams params) {
        params.textDocument.uri = params.textDocument.uri.replace("file://", "")

        List<Location> varReferences = getVarReferences(params)
        if(!varReferences.isEmpty()) {
            return varReferences
        }
        return getFuncReferences(params)
    }

    List<Location> getFuncReferences(ReferenceParams params) {
        Set<FuncDefinition> definitions = storage.getFuncDefinitions()
        if (definitions == null) {
            return []
        }
        FuncDefinition definition = findMatchingDefinition(definitions, params) as FuncDefinition
        if (definition != null) {
            Set<FuncCall> allFuncCalls = storage.getFuncCalls()
            Set<FuncCall> matchingFuncCalls = findMatchingFuncCalls(allFuncCalls, definition)
            return matchingFuncCalls.collect { it.getLocation() }.sort { it.range.start.line }
        }
        return []
    }
    private List<Location> getVarReferences(ReferenceParams params) {
        Set<VarDefinition> definitions = storage.getVarDefinitions()
        if (definitions == null) {
            return []
        }
        VarDefinition definition = findMatchingDefinition(definitions, params) as VarDefinition
        if (definition != null) {
            Set<VarUsage> allUsages = storage.getVarUsages()
            Set<VarUsage> usages = findMatchingVarUsages(allUsages, definition)
            return usages.collect { it.getLocation() }.sort { it.range.start.line }
        }
        return []
    }

    List<Location> getFuncDefinition(TextDocumentPositionParams params) {
        Set<FuncCall> references = storage.getFuncCalls()
        FuncCall matchingFuncCall = findMatchingReference(references, params) as FuncCall
        if (matchingFuncCall == null) {
            return []
        }
        Set<FuncDefinition> definitions = storage.getFuncDefinitions()
        FuncDefinition definition = findMatchingFuncDefinition(definitions, matchingFuncCall)
        if (definition == null) {
            return []
        }
        return Arrays.asList(definition.getLocation())
    }

    private List<Location> getVarDefinition(TextDocumentPositionParams params) {
        Set<VarUsage> references = storage.getVarUsages()
        VarUsage matchingUsage = findMatchingReference(references, params) as VarUsage
        if (matchingUsage == null) {
            return []
        }
        Set<VarDefinition> definitions = storage.getVarDefinitions()
        VarDefinition definition = findMatchingDefinition(definitions, matchingUsage) as VarDefinition
        if (definition == null) {
            return []
        }
        return Arrays.asList(definition.getLocation())
    }

    private List<Location> getClassDefinition(TextDocumentPositionParams params) {
        String path = params.textDocument.uri.replace("file://", "")
        Set<ClassUsage> references = storage.getClassUsages()
        ClassUsage matchingReference = findMatchingReference(references, params) as ClassUsage
        log.info "matchingReference: $matchingReference"
        if (matchingReference == null) {
            return []
        }
        ClassDefinition definition = storage.getClassDefinitions().find{ it.getFullClassName() == matchingReference.referencedClassName}
        if(definition == null) {
            return []
        }
        def start = new Position(definition.lineNumber, definition.columnNumber)
        def end = new Position(definition.lastLineNumber, definition.lastColumnNumber)
        return Arrays.asList(new Location(definition.getSourceFileURI(), new Range(start, end)))
    }

    static Set<VarUsage> findMatchingVarUsages(Set<VarUsage> varUsages, VarDefinition varDefinition) {
        return varUsages.findAll {
            it.typeName == varDefinition.typeName && it.definitionLineNumber == varDefinition.lineNumber
        }
    }

    static Set<FuncCall> findMatchingFuncCalls(Set<FuncCall> funcCalls, FuncDefinition definition) {
        funcCalls.findAll{ it.definingClass == definition.definingClass && it.functionName == definition.functionName && it.argumentTypes == definition.parameterTypes }
    }

    static FuncDefinition findMatchingFuncDefinition(Set<FuncDefinition> definitions, FuncCall reference) {
        return definitions.find {
            it.definingClass == reference.definingClass && it.functionName == reference.functionName && it.parameterTypes == reference.argumentTypes
        }
    }

    static VarDefinition findMatchingDefinition(Set<VarDefinition> definitions, VarUsage reference) {
        return definitions.find {
            it.typeName == reference.typeName && it.varName == reference.varName && it.lineNumber == reference.definitionLineNumber
        }
    }

    static <T extends HasLocation> T findMatchingReference(Set<? extends HasLocation> references, TextDocumentPositionParams params) {
        return references.find {
            it.columnNumber <= params.position.character && it.lastColumnNumber >= params.position.character && it.lineNumber <= params.position.line && it.lastLineNumber >= params.position.line
        }
    }

    static <T extends HasLocation> T findMatchingDefinition(Set<? extends HasLocation> definitions, ReferenceParams params) {
        return definitions.find {
            it.getSourceFileURI() == params.textDocument.uri && it.columnNumber <= params.position.character && it.lastColumnNumber >= params.position.character && it.lineNumber <= params.position.line && it.lastLineNumber >= params.position.line
        }
    }
}