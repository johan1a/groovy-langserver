package org.gls.lang

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.eclipse.lsp4j.Location
import org.eclipse.lsp4j.Position
import org.eclipse.lsp4j.Range
import org.eclipse.lsp4j.TextDocumentPositionParams

@Slf4j
@TypeChecked
class ReferenceStorage {

    // Key is class name
    private Map<String, ClassDefinition> classDefinitions = new HashMap<>()

    // Key is soure file uri
    private Map<String, Set<ClassReference> > classReferences = new HashMap<>()
    private Map<String, Set<VarReference> > varReferences = new HashMap<>()
    private Map<String, Set<VarDefinition> > varDefinitions = new HashMap<>()

    Map<String, ClassDefinition> getClassDefinitions() {
        return classDefinitions
    }


    void addClassDefinition(ClassDefinition definition) {
        classDefinitions.put(definition.getFullClassName(), definition)
    }

    void addClassReference(ClassReference reference) {
        Set<ClassReference> references = classReferences.get(reference.sourceFileURI)
        if(references == null) {
            references = new HashSet<>()
            classReferences.put(reference.sourceFileURI, references)
        }
        references.add(reference)
    }

    void addVarReference(VarReference reference) {
        Set<VarReference> references = varReferences.get(reference.sourceFileURI)
        if(references == null) {
            references = new HashSet<>()
            varReferences.put(reference.sourceFileURI, references)
        }
        references.add(reference)
    }

    void addVarDefinition(VarDefinition definition) {
        Set<VarDefinition> definitions = varDefinitions.get(definition.sourceFileURI)
        if(definitions == null) {
            definitions = new HashSet<>()
            varDefinitions.put(definition.sourceFileURI, definitions)
        }
        definitions.add(definition)
    }


    List<Location> getDefinition(TextDocumentPositionParams params) {
        List<Location> varDefinitions = getVarDefinition(params)
        if(varDefinitions.isEmpty()) {
            return getClassDefinition(params)
        }
        return varDefinitions
    }

    List<Location> getVarDefinition(TextDocumentPositionParams params) {
        String path = params.textDocument.uri.replace("file://", "")
        Set<VarReference> references = varReferences.get(path)
        VarReference matchingReference = findMatchingReference(references, params) as VarReference
        if (matchingReference == null) {
            return Collections.emptyList()
        }
        log.info "matchingReference: $matchingReference"
        Set<VarDefinition> definitions = varDefinitions.get(matchingReference.sourceFileURI)
        log.info "definitions: $definitions"
        VarDefinition definition = findMatchingDefinition(definitions, matchingReference) as VarDefinition
        log.info "params: $params"
        log.info "definition: $definition"
        if (definition == null) {
            return Collections.emptyList()
        }
        def start = new Position(definition.lineNumber, definition.columnNumber)
        def end = new Position(definition.lastLineNumber, definition.lastColumnNumber)
        return Arrays.asList(new Location(definition.getURI(), new Range(start, end)))
    }

    VarDefinition findMatchingDefinition(Set<VarDefinition> definitions, VarReference reference) {
        return definitions.find {
            it.typeName == reference.definitionClassName && it.varName == reference.varName && it.lineNumber == reference.definitionLineNumber
        }

    }

    List<Location> getClassDefinition(TextDocumentPositionParams params) {
        String path = params.textDocument.uri.replace("file://", "")
        Set<ClassReference> references = classReferences.get(path)
        //log.info "references: $references"
        ClassReference matchingReference = findMatchingReference(references, params) as ClassReference
        if (matchingReference == null) {
            return Collections.emptyList()
        }
        log.info "matchingReference: $matchingReference"
        ClassDefinition definition = classDefinitions.get(matchingReference.referencedClassName)
        def start = new Position(definition.lineNumber, definition.columnNumber)
        def end = new Position(definition.lastLineNumber, definition.lastColumnNumber)
        return Arrays.asList(new Location(definition.getURI(), new Range(start, end)))
    }


    Reference findMatchingReference(Set<? extends Reference> references, TextDocumentPositionParams params) {
        return references.find {
            it.columnNumber <= params.position.character && it.lastColumnNumber >= params.position.character && it.lineNumber <= params.position.line && it.lastLineNumber >= params.position.line
        }
    }

    // Fully qualified class name
    Set<ClassReference> getClassReferences(String fullClassName) {
        return classReferences.get(fullClassName)
    }
}
