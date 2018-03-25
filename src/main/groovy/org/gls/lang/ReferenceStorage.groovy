package org.gls.lang

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.GroovyCodeVisitor
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.*
import org.codehaus.groovy.classgen.*
import org.codehaus.groovy.control.CompilationUnit
import org.codehaus.groovy.ast.ClassNode
import java.util.Map
import groovy.transform.TypeChecked
import org.eclipse.lsp4j.ReferenceParams
import org.eclipse.lsp4j.Location
import org.eclipse.lsp4j.Range
import org.eclipse.lsp4j.Position
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

    void addClassDefinition(ClassDefinition definition) {
        classDefinitions.put(definition.getFullClassName(), definition)
        log.info "Added $definition"
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
        List<Location> classDefinition = getClassDefinition(params)
        if(classDefinition == null) {
            return getVarDefinition(params)
        }
    }

    List<Location> getVarDefinition(TextDocumentPositionParams params) {
        String path = params.textDocument.uri.replace("file://", "")
        Set<VarReference> references = varReferences.get(path)
        log.info "references: $references"
        VarReference matchingReference = findMatchingReference(references, params) as VarReference
        if (matchingReference == null) {
            return Collections.emptyList()
        }
        log.info "matchingReference: $matchingReference"
        Set<VarDefinition> definitions = varDefinitions.get(matchingReference.sourceFileURI)
        VarDefinition definition = findMatchingDefinition(definitions, matchingReference) as VarDefinition
        def start = new Position(definition.lineNumber, definition.columnNumber)
        def end = new Position(definition.lastLineNumber, definition.lastColumnNumber)
        return Arrays.asList(new Location(definition.getURI(), new Range(start, end)))
    }

    VarDefinition findMatchingDefinition(Set<VarDefinition> definitions, VarReference reference) {
        definitions.find {
            it.typeName == reference.definitionClassName && it.varName == reference.varName && it.lineNumber == reference.definitionLineNumber
        }

    }

    List<Location> getClassDefinition(TextDocumentPositionParams params) {
        String path = params.textDocument.uri.replace("file://", "")
        Set<ClassReference> references = classReferences.get(path)
        log.info "references: $references"
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

    VarDefinition getVarDefinition(ReferenceParams params) {


    }

}
