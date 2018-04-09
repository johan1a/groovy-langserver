package org.gls.lang

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.eclipse.lsp4j.ReferenceParams
import org.eclipse.lsp4j.TextDocumentPositionParams

@TypeChecked
@Slf4j
class VarReferenceFinder {

    ReferenceMatcher matcher = new ReferenceMatcher<VarUsage, VarDefinition>()


    List<ImmutableLocation> getVarReferences(ReferenceStorage storage, ReferenceParams params) {
        Set<VarDefinition> definitions = storage.getVarDefinitions()
        Optional<VarDefinition> definitionOptional = matcher.findMatchingDefinition(definitions, params)
        definitionOptional.map { definition ->
            Set<VarUsage> allUsages = storage.getVarUsages()
            Set<VarUsage> usages = definition.findMatchingReferences(allUsages)
            return usages.collect { it.getLocation() }.sort { it.range.start.line }
        }.orElse([])
    }

    List<ImmutableLocation> getVarDefinition(ReferenceStorage storage, TextDocumentPositionParams params) {
        Set<VarUsage> references = storage.getVarUsages()
        Optional<VarUsage> usageOptional = matcher.findMatchingReference(references, params)
        usageOptional.map { matchingUsage ->
            Set<VarDefinition> definitions = storage.getVarDefinitions()
            Optional<VarDefinition> definition = matchingUsage.findMatchingDefinition(definitions)
            definition.map {
                Arrays.asList(it.getLocation())
            }.orElse([])
        }.orElse([])
    }

}
