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
        return matcher.getReferences(storage.getClassDefinitions(), storage.getClassUsages(), params)
    }

    List<ImmutableLocation> getClassDefinition(ReferenceStorage storage, TextDocumentPositionParams params) {
        return matcher.getDefinition(storage.getClassDefinitions(), storage.getClassUsages(), params)
    }

}
