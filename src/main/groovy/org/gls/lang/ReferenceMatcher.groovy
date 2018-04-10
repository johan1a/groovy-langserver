package org.gls.lang

import org.eclipse.lsp4j.Position
import org.eclipse.lsp4j.ReferenceParams
import org.eclipse.lsp4j.TextDocumentPositionParams
import org.gls.lang.definition.Definition
import org.gls.lang.reference.Reference

class ReferenceMatcher<R extends Reference, D extends Definition> {

    static List<ImmutableLocation> getReferences(Set<D> definitions, Set<R> allReferences, ReferenceParams params) {
        Optional<D> definitionOptional = findMatchingDefinition(definitions, allReferences, params)
        definitionOptional.map { definition ->
            Set<R> references = definition.getReferences()
            return references.collect { it.getLocation() }.sort { it.range.start.line }
        }.orElse([])
    }

    static List<ImmutableLocation> getDefinition(Set<R> references, TextDocumentPositionParams params) {
        Optional<R> usageOptional = findMatchingReference(references, params)
        usageOptional.map { matchingUsage ->
            Optional<D> definition = matchingUsage.getDefinition()
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

    static Optional<D> findMatchingDefinition(Set<D> definitions, Set<R> references, ReferenceParams params) {
        Optional<D> definition = Optional.ofNullable(definitions.find {
            mathesPosition(it, params.textDocument.uri, params.position)
        })
        if (definition.isPresent()) {
            return definition
        }
        return findDefinitionOfReference(definitions, references, params)
    }

    static Optional<D> findDefinitionOfReference(Set<D> definitions, Set<R> references, ReferenceParams params) {
        Optional<R> reference = findMatchingReference(references, params)
        reference.map { it.findMatchingDefinition(definitions) }.orElse(Optional.empty())
    }

    static boolean mathesPosition(HasLocation hasLocation, String uri, Position position) {
        hasLocation.getSourceFileURI() == uri &&
                hasLocation.columnNumber <= position.character &&
                hasLocation.lastColumnNumber >= position.character &&
                hasLocation.lineNumber <= position.line &&
                hasLocation.lastLineNumber >= position.line
    }

    static void correlate(Set<D> definitions, Set<R> references) {
        definitions.each { definition ->
            Set<R> matchingReferences = definition.findMatchingReferences(references)
            definition.setReferences(matchingReferences)
            matchingReferences.each { it.setDefinition(definition) }
        }
    }
}
