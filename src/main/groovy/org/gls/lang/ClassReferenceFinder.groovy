package org.gls.lang

import org.eclipse.lsp4j.ReferenceParams
import org.eclipse.lsp4j.TextDocumentPositionParams

/**
 * Created by johan on 4/9/18.
 */
class ClassReferenceFinder {
    List<ImmutableLocation> getClassReferences(ReferenceStorage storage, ReferenceParams params) {
        Set<ClassDefinition> definitions = storage.getClassDefinitions()
        Optional<ClassDefinition> definitionOptional = findMatchingDefinition(definitions, params)
        definitionOptional.map { definition ->
            Set<ClassUsage> classUsages = storage.getClassUsages()
            Set<ClassUsage> matchingClassReferences = findMatchingReferences(classUsages, definition)
            return matchingClassReferences.collect { it.getLocation() }.sort { it.range.start.line }
        }.orElse([])
    }

    List<ImmutableLocation> getClassDefinition(ReferenceStorage storage, TextDocumentPositionParams params) {
        Set<ClassUsage> references = storage.getClassUsages()
        Optional<ClassUsage> referenceOptional = findMatchingReference(references, params)
        referenceOptional.map { matchingReference ->
            Set<ClassDefinition> definitions = storage.getClassDefinitions()
            Optional<ClassDefinition> definition = matchingReference.findMatchingDefinition(definitions)
            definition.map{
                Arrays.asList(it.getLocation())

            }.orElse([])
        }.orElse([])
    }

    static Set<ClassUsage> findMatchingReferences(Set<ClassUsage> classUsages, ClassDefinition definition) {
        classUsages.findAll {
            it.fullReferencedClassName == definition.fullClassName
        }
    }

    static <T extends HasLocation> Optional<T> findMatchingReference(Set<? extends HasLocation> references, TextDocumentPositionParams params) {
        return Optional.ofNullable(references.find {
            it.getSourceFileURI() == params.textDocument.uri &&
                    it.columnNumber <= params.position.character &&
                    it.lastColumnNumber >= params.position.character &&
                    it.lineNumber <= params.position.line &&
                    it.lastLineNumber >= params.position.line
        })
    }

    static <T extends HasLocation> Optional<T> findMatchingDefinition(Set<? extends HasLocation> definitions, ReferenceParams params) {
        return Optional.ofNullable(definitions.find {
            it.getSourceFileURI() == params.textDocument.uri &&
                    it.columnNumber <= params.position.character &&
                    it.lastColumnNumber >= params.position.character &&
                    it.lineNumber <= params.position.line &&
                    it.lastLineNumber >= params.position.line
        })
    }
}
