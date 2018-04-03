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

    void addFuncDefinition(String filePath, FuncDefinition funcDefinition) {
        storage.addFuncDefinitionToFile(filePath, funcDefinition)
    }

    void addFuncCall(FuncCall funcCall) {
        storage.addFuncCall(funcCall)
    }

    void addVarUsageByDefinition(VarUsage usage) {
        Set<VarDefinition> definitions = storage.getVarDefinitionsByFile(usage.sourceFileURI)
        VarDefinition definition = findMatchingDefinition(definitions, usage)
        if (definition != null) {
            storage.addVarUsageByDefinition(usage, definition)
        }
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
        Set<VarDefinition> definitions = storage.getVarDefinitionsByFile(uri)
        if(definitions == null) {
            return Collections.emptyList()
        }
        VarDefinition definition = findMatchingDefinition(definitions, params)
        if(definition != null) {
            Set<VarUsage> usages = storage.getVarUsagesByDefinition(definition)
            return usages.collect { it.getLocation() }.sort { it.range.start.line }
        }
        return Collections.emptyList()
    }

    List<Location> getFuncDefinition(TextDocumentPositionParams params) {
        String path = params.textDocument.uri.replace("file://", "")
        Set<FuncCall> references = storage.getFuncCallsByFile(path)
        FuncCall matchingUsage = findMatchingReference(references, params) as FuncCall
        if (matchingUsage == null) {
            return Collections.emptyList()
        }
        Set<FuncDefinition> definitions = storage.getFuncDefinitionsByFile(matchingUsage.sourceFileURI)
        FuncDefinition definition = findMatchingFuncDefinition(definitions, matchingUsage)
        if (definition == null) {
            return Collections.emptyList()
        }
        return Arrays.asList(definition.getLocation())
    }

    private List<Location> getVarDefinition(TextDocumentPositionParams params) {
        String path = params.textDocument.uri.replace("file://", "")
        Set<VarUsage> references = storage.getVarUsagesByFile(path)
        VarUsage matchingUsage = findMatchingReference(references, params) as VarUsage
        if (matchingUsage == null) {
            return Collections.emptyList()
        }
        Set<VarDefinition> definitions = storage.getVarDefinitionsByFile(matchingUsage.sourceFileURI)
        VarDefinition definition = findMatchingDefinition(definitions, matchingUsage) as VarDefinition
        if (definition == null) {
            return Collections.emptyList()
        }
        return Arrays.asList(definition.getLocation())
    }

    private List<Location> getClassDefinition(TextDocumentPositionParams params) {
        String path = params.textDocument.uri.replace("file://", "")
        Set<ClassUsage> references = storage.getClassUsagesByFile(path)
        ClassUsage matchingReference = findMatchingReference(references, params) as ClassUsage
        log.info "matchingReference: $matchingReference"
        if (matchingReference == null) {
            return Collections.emptyList()
        }
        ClassDefinition definition = storage.getClassDefinitionByName(matchingReference.referencedClassName)
        if(definition == null) {
            return Collections.emptyList()
        }
        def start = new Position(definition.lineNumber, definition.columnNumber)
        def end = new Position(definition.lastLineNumber, definition.lastColumnNumber)
        return Arrays.asList(new Location(definition.getURI(), new Range(start, end)))
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

    static Reference findMatchingReference(Set<? extends Reference> references, TextDocumentPositionParams params) {
        return references.find {
            it.columnNumber <= params.position.character && it.lastColumnNumber >= params.position.character && it.lineNumber <= params.position.line && it.lastLineNumber >= params.position.line
        }
    }

    static VarDefinition findMatchingDefinition(Set<VarDefinition> definitions, ReferenceParams params) {
        return definitions.find {
            it.columnNumber <= params.position.character && it.lastColumnNumber >= params.position.character && it.lineNumber <= params.position.line && it.lastLineNumber >= params.position.line
        }
    }
}
