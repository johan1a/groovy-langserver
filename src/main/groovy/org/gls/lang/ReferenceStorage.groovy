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
    // For finding var usages of a certain class
    private Map<String, Set<VarUsage>> classVarUsages = new HashMap<>()

    // Key is soure file uri
    private Map<String, Set<ClassUsage> > classUsages = new HashMap<>()
    private Map<String, Set<VarUsage> > varUsages = new HashMap<>()
    private Map<String, Set<VarDefinition> > varDefinitions = new HashMap<>()

    Map<String, ClassDefinition> getClassDefinitions() {
        return classDefinitions
    }
    Map<String, Set<VarUsage>> getVarUsages() {
        return varUsages
    }

    Set<ClassUsage> getClassUsages(String fileUri) {
        return classUsages.get(fileUri)
    }

    void addClassDefinition(ClassDefinition definition) {
        classDefinitions.put(definition.getFullClassName(), definition)
    }

    void addClassUsage(ClassUsage reference) {
        Set<ClassUsage> references = classUsages.get(reference.sourceFileURI)
        if(references == null) {
            references = new HashSet<>()
            classUsages.put(reference.sourceFileURI, references)
        }
        references.add(reference)
    }

    void addVarUsage(VarUsage usage) {
        Set<VarUsage> usages = varUsages.get(usage.sourceFileURI)
        if(usages == null) {
            usages = new HashSet<>()
            varUsages.put(usage.sourceFileURI, usages)
        }
        usages.add(usage)
        addClassVarUsage(usage)
    }

    void addClassVarUsage(VarUsage varUsage) {
        varUsage.getDeclaringClass().map{ name ->
            Set<VarUsage> usages = classVarUsages.get(name)
            if(usages == null) {
                usages = new HashSet<>()
                classVarUsages.put(name, usages)
            }
            usages.add(varUsage)
        }
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
        Set<VarUsage> references = varUsages.get(path)
        VarUsage matchingUsage = findMatchingReference(references, params) as VarUsage
        if (matchingUsage == null) {
            return Collections.emptyList()
        }
        log.info "matchingReference: $matchingUsage"
        Set<VarDefinition> definitions = varDefinitions.get(matchingUsage.sourceFileURI)
        log.info "definitions: $definitions"
        VarDefinition definition = findMatchingDefinition(definitions, matchingUsage) as VarDefinition
        log.info "params: $params"
        log.info "definition: $definition"
        if (definition == null) {
            return Collections.emptyList()
        }
        def start = new Position(definition.lineNumber, definition.columnNumber)
        def end = new Position(definition.lastLineNumber, definition.lastColumnNumber)
        return Arrays.asList(new Location(definition.getURI(), new Range(start, end)))
    }

    VarDefinition findMatchingDefinition(Set<VarDefinition> definitions, VarUsage reference) {
        return definitions.find {
            it.typeName == reference.typeName && it.varName == reference.varName && it.lineNumber == reference.definitionLineNumber
        }

    }

    List<Location> getClassDefinition(TextDocumentPositionParams params) {
        String path = params.textDocument.uri.replace("file://", "")
        Set<ClassUsage> references = classUsages.get(path)
        ClassUsage matchingReference = findMatchingReference(references, params) as ClassUsage
        log.info "matchingReference: $matchingReference"
        if (matchingReference == null) {
            return Collections.emptyList()
        }
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
}
