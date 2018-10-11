package org.gls.lang

import org.eclipse.lsp4j.ReferenceParams
import org.eclipse.lsp4j.TextDocumentPositionParams
import org.gls.lang.reference.Reference
import org.gls.lang.definition.Definition

class LanguageService {

    List<ImmutableLocation> getDefinition(TextDocumentPositionParams params) {
        return toLocation(getDefinitionInternal(params))
    }

    List<ImmutableLocation> getReferences(ReferenceParams params) {
        return toLocation(getReferencesInternal(params))
    }

    List<Definition> getDefinitionInternal(TextDocumentPositionParams params) {
        []
    }

    List<Reference> getReferencesInternal(ReferenceParams params) {
        []
    }

    static List<ImmutableLocation> toLocation(List<? extends HasLocation> references) {
        references*.location
                .findAll { it.range.start.line > 0 && it.range.start.character > 0 }
                .sort()
    }

}