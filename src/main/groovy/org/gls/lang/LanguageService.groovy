package org.gls.lang

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.eclipse.lsp4j.CompletionItem
import org.eclipse.lsp4j.Position
import org.eclipse.lsp4j.ReferenceParams
import org.eclipse.lsp4j.RenameParams
import org.eclipse.lsp4j.TextDocumentIdentifier
import org.eclipse.lsp4j.TextDocumentPositionParams
import org.eclipse.lsp4j.TextEdit
import org.gls.CompletionRequest
import org.gls.lang.definition.ClassDefinition
import org.gls.lang.definition.Definition
import org.gls.lang.definition.FuncDefinition
import org.gls.lang.definition.VarDefinition
import org.gls.lang.reference.ClassReference
import org.gls.lang.reference.FuncReference
import org.gls.lang.reference.VarReference
import org.gls.lang.reference.Reference

@Slf4j
@TypeChecked
class LanguageService {

    ReferenceStorage storage = new ReferenceStorage()
    ReferenceMatcher funcReferenceFinder = new ReferenceMatcher<FuncReference, FuncDefinition>()
    ReferenceMatcher varReferenceFinder = new ReferenceMatcher<VarReference, VarDefinition>()
    ReferenceMatcher classReferenceFinder = new ReferenceMatcher<ClassReference, ClassReference>()
    AutoCompleter autoCompleter = new AutoCompleter()

    Set<ClassReference> getClassReferences() {
        return storage.classReferences
    }

    void addClassDefinition(ClassDefinition definition) {
        storage.addClassDefinitionToFile(definition)
    }

    void addClassUsage(ClassReference reference) {
        storage.addClassReference(reference)
    }

    void addVarUsage(VarReference usage) {
        storage.addVarReference(usage)
    }

    void addFuncDefinition(FuncDefinition funcDefinition) {
        storage.addFuncDefinitionToFile(funcDefinition)
    }

    void addFuncCall(FuncReference funcCall) {
        storage.addFuncReference(funcCall)
    }

    void addVarDefinition(VarDefinition definition) {
        storage.addVarDefinitionToFile(definition)
    }

    List<ImmutableLocation> getDefinition(TextDocumentPositionParams params) {
        return toLocation(getDefinitionInternal(params))
    }

    List<ImmutableLocation> getReferences(ReferenceParams params) {
        return toLocation(getReferencesInternal(params))
    }

    List<Definition> getDefinitionInternal(TextDocumentPositionParams params) {
        List<Definition> varDefinitions = varReferenceFinder.getDefinitions(storage.varReferences, params)
        if (!varDefinitions.isEmpty()) {
            return varDefinitions
        }
        List<Definition> classDefinitions = classReferenceFinder.getDefinitions(storage.classReferences, params)
        if (!classDefinitions.isEmpty()) {
            return classDefinitions
        }
        return funcReferenceFinder.getDefinitions(storage.funcReferences, params)
    }

    List<Reference> getReferencesInternal(ReferenceParams params) {
        List<Reference> varReferences = varReferenceFinder.getReferences(storage.varDefinitions, storage.varReferences,
                params)
        if (!varReferences.isEmpty()) {
            return varReferences
        }
        List<Reference> classReferences = classReferenceFinder.getReferences(storage.classDefinitions,
                storage.classReferences, params)
        if (!classReferences.isEmpty()) {
            return classReferences
        }
        return funcReferenceFinder.getReferences(storage.funcDefinitions, storage.funcReferences, params)
    }

    static List<ImmutableLocation> toLocation(List<? extends HasLocation> references) {
        references*.location
                .findAll { it.range.start.line > 0 && it.range.start.character > 0 }
                .sort()
    }

    void correlate() {
        varReferenceFinder.correlate(storage.varDefinitions, storage.varReferences)
        funcReferenceFinder.correlate(storage.funcDefinitions, storage.funcReferences)
        classReferenceFinder.correlate(storage.classDefinitions, storage.classReferences)
    }

    Map<String, List<TextEdit>> rename(RenameParams params) {
        Map<String, List<TextEdit>> varEdits = varReferenceFinder.rename(storage.varDefinitions,
                storage.varReferences, params)
        if (!varEdits.isEmpty()) {
            return varEdits
        }
        Map<String, List<TextEdit>> funcEdits = funcReferenceFinder.rename(storage.funcDefinitions,
                storage.funcReferences, params)
        if (!funcEdits.isEmpty()) {
            return funcEdits
        }
        Map<String, List<TextEdit>> classEdits = classReferenceFinder.rename(storage.classDefinitions,
                storage.classReferences, params)
        return classEdits
    }

    List<CompletionItem> getCompletionItems(CompletionRequest request) {
        String precedingText = request.precedingText

        log.debug("precedingText: ${precedingText}")
        log.debug("request position: ${request.position.character}")
        log.debug("request line: ${request.position.line}")

        TextDocumentIdentifier document = new TextDocumentIdentifier(request.uri)
        Position position = new ImmutablePosition(request.position.line, request.position.character)
        TextDocumentPositionParams params = new TextDocumentPositionParams(document, position)
        List<VarDefinition> varDefinitions = varReferenceFinder.getDefinitions(storage.varReferences, params)

        log.debug("Found varDefinitions: ${varDefinitions}")

        List<ClassDefinition> classDefinitions = varDefinitions.collect { VarDefinition it ->
            storage.getClassDefinition(it.typeName)
        }
        log.debug("Found classDefinitions: ${classDefinitions}")

        List<CompletionItem> items =  classDefinitions.collectMany { autoCompleter.autoComplete(it, precedingText) }

        log.debug("Found completionitems: ${items}")
        items
    }
}

