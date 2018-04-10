package org.gls.lang

import org.eclipse.lsp4j.Position
import org.eclipse.lsp4j.ReferenceParams
import org.eclipse.lsp4j.TextDocumentPositionParams

class ReferenceMatcher<R extends Reference, D extends Definition> {

    static List<ImmutableLocation> getReferences(Set<D> definitions, Set<R> allUsages, ReferenceParams params) {
        Optional<D> definitionOptional = findMatchingDefinition(definitions, params)
        definitionOptional.map { definition ->
            Set<R> usages = definition.findMatchingReferences(allUsages)
            return usages.collect { it.getLocation() }.sort { it.range.start.line }
        }.orElse([])
    }

    static List<ImmutableLocation> getDefinition(Set<D> definitions, Set<R> references, TextDocumentPositionParams params) {
        Optional<R> usageOptional = findMatchingReference(references, params)
        usageOptional.map { matchingUsage ->
            Optional<D> definition = matchingUsage.findMatchingDefinition(definitions)
            definition.map {
                Arrays.asList(it.getLocation())
            }.orElse([])
        }.orElse([])
    }

    static Optional<R> findMatchingReference(Set<R> references, TextDocumentPositionParams params) {
        return Optional.ofNullable(references.find {
            mathesPosition(it, params.textDocument.uri, params.position)
        })
    }

    static Optional<D> findMatchingDefinition(Set<D> definitions, ReferenceParams params) {
        return Optional.ofNullable(definitions.find {
            mathesPosition(it, params.textDocument.uri, params.position)
        })
    }

    static boolean mathesPosition(HasLocation hasLocation, String uri, Position position) {
        hasLocation.getSourceFileURI() == uri &&
                hasLocation.columnNumber <= position.character &&
                hasLocation.lastColumnNumber >= position.character &&
                hasLocation.lineNumber <= position.line &&
                hasLocation.lastLineNumber >= position.line
    }

}
