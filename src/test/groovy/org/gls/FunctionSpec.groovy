package org.gls

import org.eclipse.lsp4j.*
import org.gls.groovy.GroovyIndexer
import org.gls.lang.ReferenceFinder
import spock.lang.Specification
import static org.gls.util.TestUtil.uriList

import java.nio.file.Paths

class FunctionSpec extends Specification {


    def "Function definition"() {
        given:
        ReferenceFinder finder = new ReferenceFinder()
        String dirPath = "src/test/test-files/8"

        TextDocumentPositionParams params = new TextDocumentPositionParams()
        Position position = new Position(4, 13)
        params.position = position

        String filePath = new File(dirPath + "/FunctionDefinition.groovy").getCanonicalPath()
        params.setTextDocument(new TextDocumentIdentifier(filePath))

        when:
        GroovyIndexer indexer = new GroovyIndexer(uriList(dirPath), finder)
        indexer.index()
        List<Location> definitions = finder.getDefinition(params)


        then:
        definitions.size() == 1
        definitions.first().range.start.line == 3
    }

    def "Function definition 2"() {
        given:
        ReferenceFinder finder = new ReferenceFinder()
        String dirPath = "src/test/test-files/9"

        TextDocumentPositionParams params = new TextDocumentPositionParams()
        Position position = new Position(4, 36)
        params.position = position

        String filePath = new File(dirPath + "/ClassDefinition1.groovy").getCanonicalPath()
        params.setTextDocument(new TextDocumentIdentifier(filePath))

        when:
        GroovyIndexer indexer = new GroovyIndexer(uriList(dirPath), finder)
        indexer.index()
        List<Location> definitions = finder.getDefinition(params)

        then:
        definitions.size() == 1

        Range range = definitions.first().range
        range.start.line == 7
        range.start.character == 4
    }

}
