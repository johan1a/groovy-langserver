package org.gls.lang

import groovy.util.logging.Slf4j
import org.eclipse.lsp4j.Position
import org.eclipse.lsp4j.RenameParams
import org.eclipse.lsp4j.TextDocumentPositionParams
import org.eclipse.lsp4j.TextEdit
import org.eclipse.lsp4j.Range
import org.gls.lang.definition.Definition
import org.gls.lang.reference.Reference

@Slf4j
class ReferenceMatcher<R extends Reference, D extends Definition> {

    static List<R> getReferences(ReferenceStorage storage, Set<D> definitions, Set<R> allReferences,
                                 TextDocumentPositionParams params) {
        Optional<D> definitionOptional = findMatchingDefinition(storage, definitions, allReferences, params)
        definitionOptional.map { definition ->
            definition.references.toList()
        }.orElse([])
    }

    static List<D> getDefinitions(Set<R> references, TextDocumentPositionParams params) {
        Optional<R> usageOptional = findMatchingReference(references, params)
        usageOptional.map { log.debug("usage: ${it.toString()}") }
        usageOptional.map { matchingUsage ->
            Optional<D> definition = matchingUsage.definition
            definition.map { log.debug("definition: ${it.toString()}") }
            definition.map {
                Arrays.asList(it)
            }.orElse([])
        }.orElse([])
    }

    static Optional<R> findMatchingReference(Set<R> references, TextDocumentPositionParams params) {
        return Optional.ofNullable(references.find {
            matchesPosition(it, params.textDocument.uri, params.position)
        })
    }

    static Optional<D> findMatchingDefinition(ReferenceStorage storage, Set<D> definitions, Set<R> references,
                                              TextDocumentPositionParams params) {
        Optional<D> definition = Optional.ofNullable(definitions.find {
            matchesPosition(it, params.textDocument.uri, params.position)
        })
        if (definition.isPresent()) {
            return definition
        }
        return findDefinitionOfReference(storage, definitions, references, params)
    }

    static Optional<D> findDefinitionOfReference(ReferenceStorage storage, Set<D> definitions, Set<R> references,
                                                 TextDocumentPositionParams params) {
        Optional<R> reference = findMatchingReference(references, params)
        reference.map { it.findMatchingDefinition(storage, definitions) }.orElse(Optional.empty())
    }

    static boolean matchesPosition(HasLocation hasLocation, String uri, Position position) {
        hasLocation.sourceFileURI == uri &&
                hasLocation.columnNumber <= position.character &&
                hasLocation.lastColumnNumber >= position.character &&
                hasLocation.lineNumber <= position.line &&
                hasLocation.lastLineNumber >= position.line
    }

    static void correlate(ReferenceStorage storage, Set<D> definitions, Set<R> references) {
        definitions.each { definition ->
            Set<R> matchingReferences = definition.findMatchingReferences(storage, definitions, references)
            definition.references = matchingReferences
            matchingReferences.each { it.definition = definition }
        }
    }

    static Map<String, List<TextEdit>> rename(ReferenceStorage storage, Set<D> allDefinitions, Set<R> allReferences,
                                              RenameParams params) {
        Map<String, List<TextEdit>> changes = [:]

        TextDocumentPositionParams params1 = toTextDocumentPositionParams(params)
        List<R> references = getReferences(storage, allDefinitions, allReferences, params1)
        addTextEdits(references, changes, params.newName)
        return changes
    }

    private static void addTextEdits(List<R> references, Map<String, List<TextEdit>> changes, String newName) {
        references.forEach { R reference ->
            Range range = reference.location.range
            TextEdit defEdit = new TextEdit(range, newName)
            addOrCreate(changes, reference.sourceFileURI, defEdit)
        }

        List<Optional<D>> unique = references*.definition.unique().toList()
        unique.forEach { Optional<D> definitionOptional ->
            definitionOptional.map { definition ->
                TextEdit edit = new TextEdit(definition.location.range, newName)
                addOrCreate(changes, definition.sourceFileURI, edit)
            }
        }
    }

    static void addOrCreate(Map<String, List<TextEdit>> changes, String sourceUri, TextEdit textEdit) {
        List<TextEdit> edits = changes.get(sourceUri)
        if (edits == null) {
            edits = new LinkedList<>()
            changes.put(sourceUri, edits)
        }
        if (textEdit.range.start.line > 0 && !edits.contains(textEdit)) {
            edits.add(textEdit)
        }
    }

    private static TextDocumentPositionParams toTextDocumentPositionParams(RenameParams params) {
        new TextDocumentPositionParams(params.textDocument, params.textDocument.uri, params.position)
    }
}
