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
        Set<FuncDefinition> definitions = storage.getFuncDefinitions()
        Optional<FuncDefinition> definitionOptional = matcher.findMatchingDefinition(definitions, params)
        definitionOptional.map { definition ->
            Set<FuncCall> allFuncCalls = storage.getFuncCalls()
            Set<FuncCall> matchingFuncCalls = definition.findMatchingReferences(allFuncCalls)
            return matchingFuncCalls.collect { it.getLocation() }.sort { it.range.start.line }
        }.orElse([])
    }

    List<ImmutableLocation> getFuncDefinition(ReferenceStorage storage, TextDocumentPositionParams params) {
        Set<FuncCall> references = storage.getFuncCalls()
        Optional<FuncCall> funcCallOptional = matcher.findMatchingReference(references, params)
        funcCallOptional.map { funcCall ->
            Set<FuncDefinition> definitions = storage.getFuncDefinitions()
            Optional<FuncDefinition> definition = funcCall.findMatchingDefinition(definitions)
            definition.map {
                Arrays.asList(it.getLocation())
            }.orElse([])
        }.orElse([])
    }


}
