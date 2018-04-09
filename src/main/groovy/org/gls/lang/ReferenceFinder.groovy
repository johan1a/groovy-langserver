package org.gls.lang

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.eclipse.lsp4j.ReferenceParams
import org.eclipse.lsp4j.TextDocumentPositionParams

@Slf4j
@TypeChecked
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

    List<ImmutableLocation> getDefinition(TextDocumentPositionParams params) {
        List<ImmutableLocation> varDefinitions = getVarDefinition(params)
        log.info("varDefinitions: ${varDefinitions}")
        if (!varDefinitions.isEmpty()) {
            return varDefinitions
        }
        List<ImmutableLocation> classDefinitions = getClassDefinition(params)
        log.info("classDefinitions: ${classDefinitions}")
        if (!classDefinitions.isEmpty()) {
            return classDefinitions
        }
        return getFuncDefinition(params)
    }

    List<ImmutableLocation> getReferences(ReferenceParams params) {
        List<ImmutableLocation> varReferences = getVarReferences(params)
        if (!varReferences.isEmpty()) {
            return varReferences
        }
        List<ImmutableLocation> classReferences = getClassReferences(params)
        if (!classReferences.isEmpty()) {
            return classReferences
        }
        return getFuncReferences(params)
    }

    List<ImmutableLocation> getClassReferences(ReferenceParams params) {
        Set<ClassDefinition> definitions = storage.getClassDefinitions()
        if (definitions == null) {
            return []
        }
        ClassDefinition definition = findMatchingDefinition(definitions, params) as ClassDefinition
        if (definition != null) {
            Set<ClassUsage> classUsages = storage.getClassUsages()
            Set<ClassUsage> matchingClassReferences = findMatchingClassUsages(classUsages, definition)
            return matchingClassReferences.collect { it.getLocation() }.sort { it.range.start.line }
        }
        return []
    }

    List<ImmutableLocation> getFuncReferences(ReferenceParams params) {
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

    private List<ImmutableLocation> getVarReferences(ReferenceParams params) {
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

    List<ImmutableLocation> getFuncDefinition(TextDocumentPositionParams params) {
        Set<FuncCall> references = storage.getFuncCalls()
        FuncCall matchingFuncCall = findMatchingReference(references, params) as FuncCall
        log.info "matching func ref: $matchingFuncCall"
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

    private List<ImmutableLocation> getVarDefinition(TextDocumentPositionParams params) {
        Set<VarUsage> references = storage.getVarUsages()
        log.info "var refs size: ${references.size()}"
        references.findAll{ it.varName == "storage"}.each { log.info("debug print: $it")}
        VarUsage matchingUsage = findMatchingReference(references, params) as VarUsage
        log.info "matching var ref: $matchingUsage"
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

    private List<ImmutableLocation> getClassDefinition(TextDocumentPositionParams params) {
        Set<ClassUsage> references = storage.getClassUsages()
        ClassUsage matchingReference = findMatchingReference(references, params) as ClassUsage
        log.info "matching class ref: $matchingReference"
        if (matchingReference == null) {
            return []
        }
        ClassDefinition definition = storage.getClassDefinitions().find {
            it.getFullClassName() == matchingReference.fullReferencedClassName
        }
        if (definition == null) {
            return []
        }
        return Arrays.asList(definition.getLocation())
    }

    static Set<VarUsage> findMatchingVarUsages(Set<VarUsage> varUsages, VarDefinition varDefinition) {
        return varUsages.findAll {
            it.getSourceFileURI() == varDefinition.getSourceFileURI() &&
                    it.typeName == varDefinition.typeName &&
                    it.definitionLineNumber == varDefinition.lineNumber
        }
    }

    static Set<ClassUsage> findMatchingClassUsages(Set<ClassUsage> classUsages, ClassDefinition definition) {
        classUsages.findAll {
            it.fullReferencedClassName == definition.fullClassName
        }
    }


    static Set<FuncCall> findMatchingFuncCalls(Set<FuncCall> funcCalls, FuncDefinition definition) {
        funcCalls.findAll {
            it.definingClass == definition.definingClass &&
                    it.functionName == definition.functionName &&
                    it.argumentTypes == definition.parameterTypes
        }
    }

    static FuncDefinition findMatchingFuncDefinition(Set<FuncDefinition> definitions, FuncCall reference) {
        return definitions.find {
            it.definingClass == reference.definingClass &&
                    it.functionName == reference.functionName &&
                    it.parameterTypes == reference.argumentTypes
        }
    }

    static VarDefinition findMatchingDefinition(Set<VarDefinition> definitions, VarUsage reference) {
        return definitions.find {
            it.getSourceFileURI() == reference.getSourceFileURI() &&
                    it.typeName == reference.typeName &&
                    it.varName == reference.varName &&
                    it.lineNumber == reference.definitionLineNumber
        }
    }

    static <T extends HasLocation> T findMatchingReference(Set<? extends HasLocation> references, TextDocumentPositionParams params) {
        return references.find {
            it.getSourceFileURI() == params.textDocument.uri &&
            it.columnNumber <= params.position.character &&
                    it.lastColumnNumber >= params.position.character &&
                    it.lineNumber <= params.position.line &&
                    it.lastLineNumber >= params.position.line
        }
    }

    static <T extends HasLocation> T findMatchingDefinition(Set<? extends HasLocation> definitions, ReferenceParams params) {
        return definitions.find {
            it.getSourceFileURI() == params.textDocument.uri &&
                    it.columnNumber <= params.position.character &&
                    it.lastColumnNumber >= params.position.character &&
                    it.lineNumber <= params.position.line &&
                    it.lastLineNumber >= params.position.line
        }
    }
}
