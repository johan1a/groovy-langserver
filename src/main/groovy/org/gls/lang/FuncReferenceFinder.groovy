package org.gls.lang

import org.eclipse.lsp4j.ReferenceParams
import org.eclipse.lsp4j.TextDocumentPositionParams

/**
 * Created by johan on 4/9/18.
 */
class FuncReferenceFinder {
    List<ImmutableLocation> getFuncReferences(ReferenceStorage storage, ReferenceParams params) {
        Set<FuncDefinition> definitions = storage.getFuncDefinitions()
        Optional<FuncDefinition> definitionOptional = findMatchingDefinition(definitions, params)
        definitionOptional.map { definition ->
            Set<FuncCall> allFuncCalls = storage.getFuncCalls()
            Set<FuncCall> matchingFuncCalls = findMatchingFuncCalls(allFuncCalls, definition)
            return matchingFuncCalls.collect { it.getLocation() }.sort { it.range.start.line }
        }.orElse([])
    }

    List<ImmutableLocation> getFuncDefinition(ReferenceStorage storage, TextDocumentPositionParams params) {
        Set<FuncCall> references = storage.getFuncCalls()
        Optional<FuncCall> funcCallOptional = findMatchingReference(references, params)
        funcCallOptional.map { funcCall ->
            Set<FuncDefinition> definitions = storage.getFuncDefinitions()
            Optional<FuncDefinition> definition = findMatchingDefinition(definitions, funcCall)
            definition.map {
                Arrays.asList(it.getLocation())
            }.orElse([])
        }.orElse([])
    }

    static Set<FuncCall> findMatchingFuncCalls(Set<FuncCall> funcCalls, FuncDefinition definition) {
        funcCalls.findAll {
            it.definingClass == definition.definingClass &&
                    it.functionName == definition.functionName &&
                    it.argumentTypes == definition.parameterTypes
        }
    }

    static Optional<FuncCall> findMatchingReference(Set<FuncCall> references, TextDocumentPositionParams params) {
        return Optional.ofNullable(references.find {
            it.getSourceFileURI() == params.textDocument.uri &&
                    it.columnNumber <= params.position.character &&
                    it.lastColumnNumber >= params.position.character &&
                    it.lineNumber <= params.position.line &&
                    it.lastLineNumber >= params.position.line
        })
    }

    static Optional<FuncDefinition> findMatchingDefinition(Set<FuncDefinition> definitions, FuncCall reference) {
        return Optional.ofNullable(definitions.find {
            it.definingClass == reference.definingClass &&
                    it.functionName == reference.functionName &&
                    it.parameterTypes == reference.argumentTypes
        })
    }

    static Optional<FuncDefinition> findMatchingDefinition(Set<FuncDefinition> definitions, ReferenceParams params) {
        return Optional.ofNullable(definitions.find {
            it.getSourceFileURI() == params.textDocument.uri &&
                    it.columnNumber <= params.position.character &&
                    it.lastColumnNumber >= params.position.character &&
                    it.lineNumber <= params.position.line &&
                    it.lastLineNumber >= params.position.line
        })
    }

}
