package org.gls.lang

import org.eclipse.lsp4j.*
import org.gls.lang.definition.Definition
import org.gls.lang.reference.Reference

class ReferenceMatcher<R extends Reference, D extends Definition> {

    static List<ImmutableLocation> getReferenceLocations(Set<D> definitions, Set<R> references, ReferenceParams params) {
        return getReferences(definitions, references, params).collect { it.getLocation() }
                .findAll{ it.range.start.line > 0 && it.range.start.character > 0}
                .sort { it.range.start.line }
    }

    static List<ImmutableLocation> getDefinitionLocations(Set<R> references, TextDocumentPositionParams params) {
        return getDefinitions(references, params).collect { it.getLocation() }
                .findAll{ it.range.start.line > 0 && it.range.start.character > 0}
                .sort { it.range.start.line }
    }

    static List<R> getReferences(Set<D> definitions, Set<R> allReferences, ReferenceParams params) {
        Optional<D> definitionOptional = findMatchingDefinition(definitions, allReferences, params)
        definitionOptional.map { definition ->
            definition.getReferences().toList()
        }.orElse([])
    }

    static List<D> getDefinitions(Set<R> references, TextDocumentPositionParams params) {
        Optional<R> usageOptional = findMatchingReference(references, params)
        usageOptional.map { matchingUsage ->
            Optional<D> definition = matchingUsage.getDefinition()
            definition.map {
                Arrays.asList(it)
            }.orElse([])
        }.orElse([])
    }

    static Optional<R> findMatchingReference(Set<R> references, TextDocumentPositionParams params) {
        return Optional.ofNullable(references.find {
            mathesPosition(it, params.textDocument.uri, params.position)
        })
    }

    static Optional<D> findMatchingDefinition(Set<D> definitions, Set<R> references, TextDocumentPositionParams params) {
        Optional<D> definition = Optional.ofNullable(definitions.find {
            mathesPosition(it, params.textDocument.uri, params.position)
        })
        if (definition.isPresent()) {
            return definition
        }
        return findDefinitionOfReference(definitions, references, params)
    }

    static Optional<D> findDefinitionOfReference(Set<D> definitions, Set<R> references, TextDocumentPositionParams params) {
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

    static Map<String, List<TextEdit>> rename(Set<R> references, RenameParams params) {
        Map<String, List<TextEdit>> changes = new HashMap<>()
        List<Definition> definitions = getDefinitions(references, toTextDocumentPositionParams(params))
        addTextEdits(definitions, changes, params.newName)
        return changes
    }

    private static void addTextEdits(List<D> definitions, Map<String, List<TextEdit>> changes, String newName) {
        definitions.forEach() { D definition ->
            Range range = definition.location.range
            TextEdit defEdit = new TextEdit(range, newName)
            addOrCreate(changes, definition.getSourceFileURI(), defEdit)
            definition.getReferences().each { R reference ->
                TextEdit edit = new TextEdit(reference.location.range, newName)
                addOrCreate(changes, reference.getSourceFileURI(), edit)
            }
        }
    }

    static void addOrCreate(Map<String, List<TextEdit>> changes, String sourceUri, TextEdit textEdit) {
        List<TextEdit> edits = changes.get(sourceUri)
        if(edits == null){
            edits = new LinkedList<>()
            changes.put(sourceUri, edits)
        }
        edits.add(textEdit)
    }

    private static TextDocumentPositionParams toTextDocumentPositionParams(RenameParams params) {
        new TextDocumentPositionParams(params.textDocument, params.textDocument.uri, params.position)
    }
}
