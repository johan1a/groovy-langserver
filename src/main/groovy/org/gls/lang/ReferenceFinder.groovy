package org.gls.lang

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.eclipse.lsp4j.ReferenceParams
import org.eclipse.lsp4j.TextDocumentPositionParams

@Slf4j
@TypeChecked
class ReferenceFinder {

    ReferenceStorage storage = new ReferenceStorage()
    VarReferenceFinder varReferenceFinder = new VarReferenceFinder()
    FuncReferenceFinder funcReferenceFinder = new FuncReferenceFinder()
    ClassReferenceFinder classReferenceFinder = new ClassReferenceFinder()

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
        List<ImmutableLocation> varDefinitions = varReferenceFinder.getDefinition(storage, params)
        log.info("varDefinitions: ${varDefinitions}")
        if (!varDefinitions.isEmpty()) {
            return varDefinitions
        }
        List<ImmutableLocation> classDefinitions = classReferenceFinder.getClassDefinition(storage, params)
        log.info("classDefinitions: ${classDefinitions}")
        if (!classDefinitions.isEmpty()) {
            return classDefinitions
        }
        return funcReferenceFinder.getFuncDefinition(storage, params)
    }

    List<ImmutableLocation> getReferences(ReferenceParams params) {
        List<ImmutableLocation> varReferences = varReferenceFinder.getReferences(storage, params)
        if (!varReferences.isEmpty()) {
            return varReferences
        }
        List<ImmutableLocation> classReferences = classReferenceFinder.getClassReferences(storage, params)
        if (!classReferences.isEmpty()) {
            return classReferences
        }
        return funcReferenceFinder.getFuncReferences(storage, params)
    }
}
