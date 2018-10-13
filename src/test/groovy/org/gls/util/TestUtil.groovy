package org.gls.util

import org.eclipse.lsp4j.Diagnostic
import org.eclipse.lsp4j.Location
import org.eclipse.lsp4j.Position
import org.eclipse.lsp4j.ReferenceParams
import org.eclipse.lsp4j.TextDocumentIdentifier
import org.gls.IndexerConfig
import org.gls.groovy.GroovyCompilerService
import org.gls.lang.ImmutablePosition
import org.gls.lang.LanguageService
import java.nio.file.Paths

class TestUtil {
    static URI uri(String path) {
        return Paths.get(path).toUri()
    }

    static boolean testReference(String directory, String fileName, List<Integer> queryPosition,
                                 List<List<Integer>> expectedResultPositions) {
        LanguageService finder = new LanguageService()
        String dirPath = "src/test/test-files/${directory}"

        ReferenceParams params = new ReferenceParams()
        Position position = new ImmutablePosition(queryPosition[0], queryPosition[1])
        params.position = position

        String filePath = new File(dirPath + "/${fileName}").canonicalPath
        params.textDocument = new TextDocumentIdentifier(filePath)

        GroovyCompilerService indexer = new GroovyCompilerService(uri(dirPath), finder, new IndexerConfig())
        Map<String, List<Diagnostic>> errors = indexer.compile()
        List<Location> references = finder.getReferences(params)

        errors.isEmpty()
        expectedResultPositions.each { pos ->
            ImmutablePosition expectedPosition = new ImmutablePosition(pos[0], pos[1])
            assert references.find { ref ->
                ref.range.start == expectedPosition
            }
        }
    }
}
