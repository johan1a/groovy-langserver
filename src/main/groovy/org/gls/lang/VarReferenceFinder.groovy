package org.gls.lang

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.eclipse.lsp4j.ReferenceParams
import org.eclipse.lsp4j.TextDocumentPositionParams

/**
 * Created by johan on 4/9/18.
 */
@TypeChecked
@Slf4j
class VarReferenceFinder {
    List<ImmutableLocation> getVarReferences(ReferenceStorage storage, ReferenceParams params) {
        Set<VarDefinition> definitions = storage.getVarDefinitions()
        Optional<VarDefinition> definitionOptional = findMatchingDefinition(definitions, params) as Optional<VarDefinition>
        definitionOptional.map { definition ->
            Set<VarUsage> allUsages = storage.getVarUsages()
            Set<VarUsage> usages = findMatchingVarUsages(allUsages, definition)
            return usages.collect { it.getLocation() }.sort { it.range.start.line }
        }.orElse([])
    }

    List<ImmutableLocation> getVarDefinition(ReferenceStorage storage, TextDocumentPositionParams params) {
        Set<VarUsage> references = storage.getVarUsages()
        references.findAll { it.varName == "storage" }.each { log.info("debug print: $it") }
        Optional<VarUsage> usageOptional = findMatchingReference(references, params) as Optional<VarUsage>
        usageOptional.map { matchingUsage ->
            Set<VarDefinition> definitions = storage.getVarDefinitions()
            Optional<VarDefinition> definition = findMatchingDefinition(definitions, matchingUsage) as Optional<VarDefinition>
            definition.map {
                Arrays.asList(it.getLocation())
            }.orElse([])
        }.orElse([])
    }

    private
    static <T extends HasLocation> Optional<T> findMatchingReference(Set<? extends HasLocation> references, TextDocumentPositionParams params) {
        return Optional.ofNullable(references.find {
            it.getSourceFileURI() == params.textDocument.uri &&
                    it.columnNumber <= params.position.character &&
                    it.lastColumnNumber >= params.position.character &&
                    it.lineNumber <= params.position.line &&
                    it.lastLineNumber >= params.position.line
        })
    }

    private static Set<VarUsage> findMatchingVarUsages(Set<VarUsage> varUsages, VarDefinition varDefinition) {
        return varUsages.findAll {
            it.getSourceFileURI() == varDefinition.getSourceFileURI() &&
                    it.typeName == varDefinition.typeName &&
                    it.definitionLineNumber == varDefinition.lineNumber
        }
    }

    private
    static Optional<VarDefinition> findMatchingDefinition(Set<VarDefinition> definitions, ReferenceParams params) {
        return Optional.ofNullable(definitions.find {
            it.getSourceFileURI() == params.textDocument.uri &&
                    it.columnNumber <= params.position.character &&
                    it.lastColumnNumber >= params.position.character &&
                    it.lineNumber <= params.position.line &&
                    it.lastLineNumber >= params.position.line
        })
    }

    private static Optional<VarDefinition> findMatchingDefinition(Set<VarDefinition> definitions, VarUsage reference) {
        return Optional.ofNullable(definitions.find {
            it.getSourceFileURI() == reference.getSourceFileURI() &&
                    it.typeName == reference.typeName &&
                    it.varName == reference.varName &&
                    it.lineNumber == reference.definitionLineNumber
        })
    }


}
