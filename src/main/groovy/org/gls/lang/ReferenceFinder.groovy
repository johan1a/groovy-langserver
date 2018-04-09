package org.gls.lang

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.eclipse.lsp4j.ReferenceParams
import org.eclipse.lsp4j.TextDocumentPositionParams

@Slf4j
@TypeChecked
class ReferenceFinder {

    ReferenceStorage storage = new ReferenceStorage()
    VarReferenceFinder varReferenceFinder = new VarReferenceFinder()
    FuncReferenceFinder funcReferenceFinder = new FuncReferenceFinder()

    Set<ClassUsage> getClassUsages(String fileUri) {
        return storage.getClassUsages()
    }

    void addClassDefinition(ClassDefinition definition) {
        storage.addClassDefinitionToFile(definition)
    }

    void addClassUsage(ClassUsage reference) {
        storage.addClassUsage(reference)
    }

    void addVarUsage(VarUsage usage) {
        storage.addVarUsage(usage)
    }

    void addFuncDefinition(FuncDefinition funcDefinition) {
        storage.addFuncDefinitionToFile(funcDefinition)
    }

    void addFuncCall(FuncCall funcCall) {
        storage.addFuncCall(funcCall)
    }

    void addVarDefinition(VarDefinition definition) {
        storage.addVarDefinitionToFile(definition)
    }

    List<ImmutableLocation> getDefinition(TextDocumentPositionParams params) {
        List<ImmutableLocation> varDefinitions = varReferenceFinder.getVarDefinition(storage, params)
        log.info("varDefinitions: ${varDefinitions}")
        if (!varDefinitions.isEmpty()) {
            return varDefinitions
        }
        List<ImmutableLocation> classDefinitions = getClassDefinition(params)
        log.info("classDefinitions: ${classDefinitions}")
        if (!classDefinitions.isEmpty()) {
            return classDefinitions
        }
        return funcReferenceFinder.getFuncDefinition(storage, params)
    }

    List<ImmutableLocation> getReferences(ReferenceParams params) {
        List<ImmutableLocation> varReferences = varReferenceFinder.getVarReferences(storage, params)
        if (!varReferences.isEmpty()) {
            return varReferences
        }
        List<ImmutableLocation> classReferences = getClassReferences(params)
        if (!classReferences.isEmpty()) {
            return classReferences
        }
        return funcReferenceFinder.getFuncReferences(storage, params)
    }

    List<ImmutableLocation> getClassReferences(ReferenceParams params) {
        Set<ClassDefinition> definitions = storage.getClassDefinitions()
        Optional<ClassDefinition> definitionOptional = findMatchingDefinition(definitions, params) as Optional<ClassDefinition>
        definitionOptional.map { definition ->
            Set<ClassUsage> classUsages = storage.getClassUsages()
            Set<ClassUsage> matchingClassReferences = findMatchingClassUsages(classUsages, definition)
            return matchingClassReferences.collect { it.getLocation() }.sort { it.range.start.line }
        }.orElse([])
    }

    private List<ImmutableLocation> getClassDefinition(TextDocumentPositionParams params) {
        Set<ClassUsage> references = storage.getClassUsages()
        Optional<ClassUsage> referenceOptional = findMatchingReference(references, params) as Optional<ClassUsage>
        List<ImmutableLocation> locations = referenceOptional.map { ClassUsage matchingReference ->
            List<ImmutableLocation> result
            ClassDefinition definition = storage.getClassDefinitions().find {
                it.getFullClassName() == matchingReference.fullReferencedClassName
            }
            if (definition == null) {
                result = []
            } else {
                result =  Arrays.asList(definition.getLocation())
            }
            return result
        }.orElse(new ArrayList<ImmutableLocation>())
        return locations
    }

    static Set<ClassUsage> findMatchingClassUsages(Set<ClassUsage> classUsages, ClassDefinition definition) {
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
