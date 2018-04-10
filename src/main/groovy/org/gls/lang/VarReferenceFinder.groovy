package org.gls.lang

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.eclipse.lsp4j.ReferenceParams
import org.eclipse.lsp4j.TextDocumentPositionParams

@TypeChecked
@Slf4j
class VarReferenceFinder {

    ReferenceMatcher matcher = new ReferenceMatcher<VarUsage, VarDefinition>()

    List<ImmutableLocation> getReferences(ReferenceStorage storage, ReferenceParams params) {
        return matcher.getVarReferences(storage.getVarDefinitions(), storage.getVarUsages(), params)
    }

    List<ImmutableLocation> getDefinition(ReferenceStorage storage, TextDocumentPositionParams params) {
        return matcher.getVarDefinition(storage.getVarDefinitions(), storage.getVarUsages(), params)
    }

}
