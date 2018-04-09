package org.gls.lang

import org.eclipse.lsp4j.ReferenceParams
import org.eclipse.lsp4j.TextDocumentPositionParams

/**
 * Created by johan on 4/9/18.
 */
class FuncReferenceFinder {

    ReferenceMatcher matcher = new ReferenceMatcher<FuncCall, FuncDefinition>()

    List<ImmutableLocation> getFuncReferences(ReferenceStorage storage, ReferenceParams params) {
        Set<FuncDefinition> definitions = storage.getFuncDefinitions()
        Optional<FuncDefinition> definitionOptional = matcher.findMatchingDefinition(definitions, params)
        definitionOptional.map { definition ->
            Set<FuncCall> allFuncCalls = storage.getFuncCalls()
            Set<FuncCall> matchingFuncCalls = findMatchingReferences(allFuncCalls, definition)
            return matchingFuncCalls.collect { it.getLocation() }.sort { it.range.start.line }
        }.orElse([])
    }

    List<ImmutableLocation> getFuncDefinition(ReferenceStorage storage, TextDocumentPositionParams params) {
        Set<FuncCall> references = storage.getFuncCalls()
        Optional<FuncCall> funcCallOptional = matcher.findMatchingReference(references, params)
        funcCallOptional.map { funcCall ->
            Set<FuncDefinition> definitions = storage.getFuncDefinitions()
            Optional<FuncDefinition> definition = findMatchingDefinition(definitions, funcCall)
            definition.map {
                Arrays.asList(it.getLocation())
            }.orElse([])
        }.orElse([])
    }

    static Set<FuncCall> findMatchingReferences(Set<FuncCall> funcCalls, FuncDefinition definition) {
        funcCalls.findAll {
            it.definingClass == definition.definingClass &&
                    it.functionName == definition.functionName &&
                    it.argumentTypes == definition.parameterTypes
        }
    }

    static Optional<FuncDefinition> findMatchingDefinition(Set<FuncDefinition> definitions, FuncCall reference) {
        return Optional.ofNullable(definitions.find {
            it.definingClass == reference.definingClass &&
                    it.functionName == reference.functionName &&
                    it.parameterTypes == reference.argumentTypes
        })
    }

}
