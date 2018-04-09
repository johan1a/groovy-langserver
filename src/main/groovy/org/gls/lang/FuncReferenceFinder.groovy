package org.gls.lang

import org.eclipse.lsp4j.ReferenceParams
import org.eclipse.lsp4j.TextDocumentPositionParams

import java.sql.Ref

/**
 * Created by johan on 4/9/18.
 */
class FuncReferenceFinder {
    List<ImmutableLocation> getFuncReferences(ReferenceStorage storage, ReferenceParams params) {
        Set<FuncDefinition> definitions = storage.getFuncDefinitions()
        Optional<FuncDefinition> definitionOptional = findMatchingDefinition(definitions, params) as Optional<FuncDefinition>
        definitionOptional.map { definition ->
            Set<FuncCall> allFuncCalls = storage.getFuncCalls()
            Set<FuncCall> matchingFuncCalls = findMatchingFuncCalls(allFuncCalls, definition)
            return matchingFuncCalls.collect { it.getLocation() }.sort { it.range.start.line }
        }.orElse([])
    }

    List<ImmutableLocation> getFuncDefinition(ReferenceStorage storage, TextDocumentPositionParams params) {
        Set<FuncCall> references = storage.getFuncCalls()
        Optional<FuncCall> funcCallOptional = findMatchingReference(references, params) as Optional<FuncCall>
        funcCallOptional.map { funcCall ->
            Set<FuncDefinition> definitions = storage.getFuncDefinitions()
            Optional<FuncDefinition> definition = findMatchingFuncDefinition(definitions, funcCall)
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

    static Optional<FuncDefinition> findMatchingFuncDefinition(Set<FuncDefinition> definitions, FuncCall reference) {
        return Optional.ofNullable(definitions.find {
            it.definingClass == reference.definingClass &&
                    it.functionName == reference.functionName &&
                    it.parameterTypes == reference.argumentTypes
        })
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
