package org.gls.lang

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.eclipse.lsp4j.ReferenceParams
import org.eclipse.lsp4j.TextDocumentPositionParams

@TypeChecked
@Slf4j
class ClassReferenceFinder {

    ReferenceMatcher matcher = new ReferenceMatcher<ClassUsage, ClassDefinition>()

    List<ImmutableLocation> getClassReferences(ReferenceStorage storage, ReferenceParams params) {
        Set<ClassDefinition> definitions = storage.getClassDefinitions()
        Optional<ClassDefinition> definitionOptional = matcher.findMatchingDefinition(definitions, params)
        definitionOptional.map { definition ->
            Set<ClassUsage> classUsages = storage.getClassUsages()
            Set<ClassUsage> matchingClassReferences = definition.findMatchingReferences(classUsages)
            return matchingClassReferences.collect { it.getLocation() }.sort { it.range.start.line }
        }.orElse([])
    }

    List<ImmutableLocation> getClassDefinition(ReferenceStorage storage, TextDocumentPositionParams params) {
        Set<ClassUsage> references = storage.getClassUsages()
        Optional<ClassUsage> referenceOptional = matcher.findMatchingReference(references, params)
        referenceOptional.map { matchingReference ->
            Set<ClassDefinition> definitions = storage.getClassDefinitions()
            Optional<ClassDefinition> definition = matchingReference.findMatchingDefinition(definitions)
            definition.map{
                Arrays.asList(it.getLocation())

            }.orElse([])
        }.orElse([])
    }

}
