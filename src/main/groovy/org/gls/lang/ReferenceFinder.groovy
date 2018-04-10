package org.gls.lang

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.eclipse.lsp4j.ReferenceParams
import org.eclipse.lsp4j.TextDocumentPositionParams

@Slf4j
@TypeChecked
class ReferenceFinder {

    ReferenceStorage storage = new ReferenceStorage()
    ReferenceMatcher funcReferenceFinder = new ReferenceMatcher<FuncCall, FuncDefinition>()
    ReferenceMatcher varReferenceFinder = new ReferenceMatcher<VarUsage, VarDefinition>()
    ReferenceMatcher classReferenceFinder = new ReferenceMatcher<ClassUsage, ClassUsage>()

    Set<ClassUsage> getClassUsages() {
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
        List<ImmutableLocation> varDefinitions = varReferenceFinder.getDefinition(storage.getVarDefinitions(), storage.getVarUsages(), params)
        if (!varDefinitions.isEmpty()) {
            return varDefinitions
        }
        List<ImmutableLocation> classDefinitions = classReferenceFinder.getDefinition(storage.getClassDefinitions(), storage.getClassUsages(), params)
        if (!classDefinitions.isEmpty()) {
            return classDefinitions
        }
        return funcReferenceFinder.getDefinition(storage.getFuncDefinitions(), storage.getFuncCalls(), params)
    }

    List<ImmutableLocation> getReferences(ReferenceParams params) {
        List<ImmutableLocation> varReferences = varReferenceFinder.getReferences(storage.getVarDefinitions(), storage.getVarUsages(), params)
        if (!varReferences.isEmpty()) {
            return varReferences
        }
        List<ImmutableLocation> classReferences = classReferenceFinder.getReferences(storage.getClassDefinitions(), storage.getClassUsages(), params)
        if (!classReferences.isEmpty()) {
            return classReferences
        }
        return funcReferenceFinder.getReferences(storage.getFuncDefinitions(), storage.getFuncCalls(), params)
    }
}
