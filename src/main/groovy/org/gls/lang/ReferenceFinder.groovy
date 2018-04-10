package org.gls.lang

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.eclipse.lsp4j.ReferenceParams
import org.eclipse.lsp4j.RenameParams
import org.eclipse.lsp4j.TextDocumentPositionParams
import org.eclipse.lsp4j.WorkspaceEdit
import org.gls.lang.definition.ClassDefinition
import org.gls.lang.definition.FuncDefinition
import org.gls.lang.definition.VarDefinition
import org.gls.lang.reference.ClassReference
import org.gls.lang.reference.FuncReference
import org.gls.lang.reference.VarReference

@Slf4j
@TypeChecked
class ReferenceFinder {

    ReferenceStorage storage = new ReferenceStorage()
    ReferenceMatcher funcReferenceFinder = new ReferenceMatcher<FuncReference, FuncDefinition>()
    ReferenceMatcher varReferenceFinder = new ReferenceMatcher<VarReference, VarDefinition>()
    ReferenceMatcher classReferenceFinder = new ReferenceMatcher<ClassReference, ClassReference>()

    Set<ClassReference> getClassReferences() {
        return storage.getClassReferences()
    }

    void addClassDefinition(ClassDefinition definition) {
        storage.addClassDefinitionToFile(definition)
    }

    void addClassUsage(ClassReference reference) {
        storage.addClassReference(reference)
    }

    void addVarUsage(VarReference usage) {
        storage.addVarReference(usage)
    }

    void addFuncDefinition(FuncDefinition funcDefinition) {
        storage.addFuncDefinitionToFile(funcDefinition)
    }

    void addFuncCall(FuncReference funcCall) {
        storage.addFuncReference(funcCall)
    }

    void addVarDefinition(VarDefinition definition) {
        storage.addVarDefinitionToFile(definition)
    }

    List<ImmutableLocation> getDefinition(TextDocumentPositionParams params) {
        List<ImmutableLocation> varDefinitions = varReferenceFinder.getDefinition(storage.getVarReferences(), params)
        if (!varDefinitions.isEmpty()) {
            return varDefinitions
        }
        List<ImmutableLocation> classDefinitions = classReferenceFinder.getDefinition(storage.getClassReferences(), params)
        if (!classDefinitions.isEmpty()) {
            return classDefinitions
        }
        return funcReferenceFinder.getDefinition(storage.getFuncReferences(), params)
    }

    List<ImmutableLocation> getReferences(ReferenceParams params) {
        List<ImmutableLocation> varReferences = varReferenceFinder.getReferenceLocations(storage.getVarDefinitions(), storage.getVarReferences(), params)
        if (!varReferences.isEmpty()) {
            return varReferences
        }
        List<ImmutableLocation> classReferences = classReferenceFinder.getReferenceLocations(storage.getClassDefinitions(), storage.getClassReferences(), params)
        if (!classReferences.isEmpty()) {
            return classReferences
        }

        List<ImmutableLocation> references = funcReferenceFinder.getReferenceLocations(storage.getFuncDefinitions(), storage.getFuncReferences(), params)
        return references
    }

    void correlate() {
        varReferenceFinder.correlate(storage.getVarDefinitions(), storage.getVarReferences())
        funcReferenceFinder.correlate(storage.getFuncDefinitions(), storage.getFuncReferences())
        classReferenceFinder.correlate(storage.getClassDefinitions(), storage.getClassReferences())
    }

    WorkspaceEdit rename(RenameParams params) {


    }
}




