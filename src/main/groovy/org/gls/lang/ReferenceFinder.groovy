package org.gls.lang

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.eclipse.lsp4j.Location
import org.eclipse.lsp4j.Position
import org.eclipse.lsp4j.Range
import org.eclipse.lsp4j.*

@Slf4j
@TypeChecked
class ReferenceFinder {

    ReferenceStorage storage = new ReferenceStorage()

    Set<ClassUsage> getClassUsages(String fileUri) {
        return storage.getClassUsagesByFile(fileUri)
    }

    void addClassDefinition(ClassDefinition definition) {
        storage.addClassDefinitionToFile(definition.getFullClassName(), definition)
    }

    void addClassUsage(ClassUsage reference) {
        storage.addClassUsage(reference)
    }

    void addVarUsage(VarUsage usage) {
        storage.addVarUsage(usage)
        addVarUsageByDefinition(usage)
    }

    private void addVarUsageByDefinition(VarUsage usage) {
        Set<VarDefinition> definitions = storage.getVarDefinitionsByFile(usage.sourceFileURI)
        VarDefinition definition = findMatchingDefinition(definitions, usage)
        if (definition != null) {
            storage.addVarUsageByDefinition(usage, definition)
        }
    }

    void addFuncDefinition(String filePath, FuncDefinition funcDefinition) {
        storage.addFuncDefinitionToFile(filePath, funcDefinition)
    }

    void addFuncCall(FuncCall funcCall) {
        storage.addFuncCall(funcCall)
    }

    void addVarDefinition(VarDefinition definition) {
        storage.addVarDefinitionToFile(definition.sourceFileURI, definition)
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
        String uri = params.textDocument.uri.replace("file://", "")

        List<Location> varReferences = getVarReferences(uri, params)
        if(!varReferences.isEmpty()) {
            return varReferences
        }
        return getFuncReferences(uri, params)
    }

    List<Location> getFuncReferences(String uri, ReferenceParams params) {
        Set<FuncDefinition> definitions = storage.getFuncDefinitionsByFile(uri)
        if (definitions == null) {
            return []
        }
        FuncDefinition definition = findMatchingDefinition(definitions, params) as FuncDefinition
        if (definition != null) {
            Set<FuncCall> allFuncCalls = storage.getAllFuncCalls()
            Set<FuncCall> matchingFuncCalls = findMatchingFuncCalls(allFuncCalls, definition)
            return matchingFuncCalls.collect { it.getLocation() }.sort { it.range.start.line }
        }
        return []
    }
    private List<Location> getVarReferences(String uri, ReferenceParams params) {
        Set<VarDefinition> definitions = storage.getVarDefinitionsByFile(uri)
        if (definitions == null) {
            return []
        }
        VarDefinition definition = findMatchingDefinition(definitions, params) as VarDefinition
        if (definition != null) {
            Set<VarUsage> usages = storage.getVarUsagesByDefinition(definition)
            return usages.collect { it.getLocation() }.sort { it.range.start.line }
        }
        return []
    }

    List<Location> getFuncDefinition(TextDocumentPositionParams params) {
        String path = params.textDocument.uri.replace("file://", "")
        Set<FuncCall> references = storage.getFuncCallsByFile(path)
        FuncCall matchingFuncCall = findMatchingReference(references, params) as FuncCall
        if (matchingFuncCall == null) {
            return []
        }
        Set<FuncDefinition> definitions = storage.getFuncDefinitionsByFile(matchingFuncCall.sourceFileURI)
        FuncDefinition definition = findMatchingFuncDefinition(definitions, matchingFuncCall)
        if (definition == null) {
            return []
        }
        return Arrays.asList(definition.getLocation())
    }

    private List<Location> getVarDefinition(TextDocumentPositionParams params) {
        String path = params.textDocument.uri.replace("file://", "")
        Set<VarUsage> references = storage.getVarUsagesByFile(path)
        VarUsage matchingUsage = findMatchingReference(references, params) as VarUsage
        if (matchingUsage == null) {
            return []
        }
        Set<VarDefinition> definitions = storage.getVarDefinitionsByFile(matchingUsage.sourceFileURI)
        VarDefinition definition = findMatchingDefinition(definitions, matchingUsage) as VarDefinition
        if (definition == null) {
            return []
        }
        return Arrays.asList(definition.getLocation())
    }

    private List<Location> getClassDefinition(TextDocumentPositionParams params) {
        String path = params.textDocument.uri.replace("file://", "")
        Set<ClassUsage> references = storage.getClassUsagesByFile(path)
        ClassUsage matchingReference = findMatchingReference(references, params) as ClassUsage
        log.info "matchingReference: $matchingReference"
        if (matchingReference == null) {
            return []
        }
        ClassDefinition definition = storage.getClassDefinitionByName(matchingReference.referencedClassName)
        if(definition == null) {
            return []
        }
        def start = new Position(definition.lineNumber, definition.columnNumber)
        def end = new Position(definition.lastLineNumber, definition.lastColumnNumber)
        return Arrays.asList(new Location(definition.getSourceFileURI(), new Range(start, end)))
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
            it.columnNumber <= params.position.character && it.lastColumnNumber >= params.position.character && it.lineNumber <= params.position.line && it.lastLineNumber >= params.position.line
        }
    }
}
