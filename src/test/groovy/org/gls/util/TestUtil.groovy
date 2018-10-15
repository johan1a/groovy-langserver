package org.gls.util

import org.eclipse.lsp4j.Diagnostic
import org.eclipse.lsp4j.Location
import org.eclipse.lsp4j.ReferenceParams
import org.eclipse.lsp4j.TextDocumentIdentifier
import org.eclipse.lsp4j.TextDocumentPositionParams
import org.gls.IndexerConfig
import org.gls.groovy.GroovyCompilerService
import org.gls.lang.ImmutablePosition
import org.gls.lang.LanguageService
import java.nio.file.Paths

@SuppressWarnings(["DuplicateNumberLiteral", "DuplicateListLiteral"])
class TestUtil {
    static URI uri(String path) {
        return Paths.get(path).toUri()
    }

    static boolean testDeclaration(String directory, String queriedFile, List<Integer> queryPosition,
                                   List<Integer> expectedPosition) {
        LanguageService languageService = new LanguageService()
        String dirPath = "src/test/test-files/${directory}"

        TextDocumentPositionParams params = new TextDocumentPositionParams()
        params.position = new ImmutablePosition(queryPosition[0], queryPosition[1])

        String filePath = new File(dirPath + "/${queriedFile}").canonicalPath
        params.textDocument = new TextDocumentIdentifier(filePath)

        GroovyCompilerService indexer = new GroovyCompilerService(uri(dirPath), languageService, new IndexerConfig())
        Map<String, List<Diagnostic>> errors = indexer.compile()
        List<Location> locations = languageService.getDefinition(params)

        errors.isEmpty()
        locations.find { location ->
            String referenceFile = getReferenceFile(expectedPosition, dirPath, queriedFile)
            ImmutablePosition position = new ImmutablePosition(expectedPosition[0], expectedPosition[1])
            location.range.start == position &&
                    location.uri == referenceFile
        }
    }

    static boolean testReference(String directory, String queriedFile, List<Integer> queryPosition,
                                 List<List<Integer>> expectedResultPositions) {
        LanguageService languageService = new LanguageService()
        String dirPath = "src/test/test-files/${directory}"

        ReferenceParams params = new ReferenceParams()
        params.position = new ImmutablePosition(queryPosition[0], queryPosition[1])

        String filePath = new File(dirPath + "/${queriedFile}").canonicalPath
        params.textDocument = new TextDocumentIdentifier(filePath)

        GroovyCompilerService indexer = new GroovyCompilerService(uri(dirPath), languageService, new IndexerConfig())
        Map<String, List<Diagnostic>> errors = indexer.compile()
        List<Location> locations = languageService.getReferences(params)

        errors.isEmpty()
        expectedResultPositions.each { pos ->
            String referenceFile = getReferenceFile(pos, dirPath, queriedFile)

            ImmutablePosition expectedPosition = new ImmutablePosition(pos[0], pos[1])
            assert locations.find { location ->
                location.range.start == expectedPosition &&
                        location.uri == referenceFile
            }
        }
    }

    private static String getReferenceFile(List expectedPos, String dirPath, String queriedFile) {
        String referenceFile
        if (expectedPos[2]) {
            referenceFile = uri("${dirPath}/${expectedPos[2]}")
        } else {
            referenceFile = uri("$dirPath/$queriedFile").toString()
        }
        referenceFile.replace("file://", "")
    }

}
