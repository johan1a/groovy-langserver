package org.gls.lang

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.eclipse.lsp4j.ReferenceParams
import org.eclipse.lsp4j.TextDocumentPositionParams

@TypeChecked
@Slf4j
class FuncReferenceFinder {

    ReferenceMatcher matcher = new ReferenceMatcher<FuncCall, FuncDefinition>()

    List<ImmutableLocation> getFuncReferences(ReferenceStorage storage, ReferenceParams params) {
        return matcher.getReferences(storage.getFuncDefinitions(), storage.getFuncCalls(), params)
    }

    List<ImmutableLocation> getFuncDefinition(ReferenceStorage storage, TextDocumentPositionParams params) {
        return matcher.getDefinition(storage.getFuncDefinitions(), storage.getFuncCalls(), params)
    }

}
