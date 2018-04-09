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

    ReferenceMatcher matcher = new ReferenceMatcher<VarUsage, VarDefinition>()


    List<ImmutableLocation> getVarReferences(ReferenceStorage storage, ReferenceParams params) {
        Set<VarDefinition> definitions = storage.getVarDefinitions()
        Optional<VarDefinition> definitionOptional = matcher.findMatchingDefinition(definitions, params)
        definitionOptional.map { definition ->
            Set<VarUsage> allUsages = storage.getVarUsages()
            Set<VarUsage> usages = findMatchingReferences(allUsages, definition)
            return usages.collect { it.getLocation() }.sort { it.range.start.line }
        }.orElse([])
    }

    List<ImmutableLocation> getVarDefinition(ReferenceStorage storage, TextDocumentPositionParams params) {
        Set<VarUsage> references = storage.getVarUsages()
        references.findAll { it.varName == "storage" }.each { log.info("debug print: $it") }
        Optional<VarUsage> usageOptional = matcher.findMatchingReference(references, params)
        usageOptional.map { matchingUsage ->
            Set<VarDefinition> definitions = storage.getVarDefinitions()
            Optional<VarDefinition> definition = findMatchingDefinition(definitions, matchingUsage)
            definition.map {
                Arrays.asList(it.getLocation())
            }.orElse([])
        }.orElse([])
    }


    private static Set<VarUsage> findMatchingReferences(Set<VarUsage> varUsages, VarDefinition varDefinition) {
        return varUsages.findAll {
            it.getSourceFileURI() == varDefinition.getSourceFileURI() &&
                    it.typeName == varDefinition.typeName &&
                    it.definitionLineNumber == varDefinition.lineNumber
        }
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
