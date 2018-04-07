package org.gls

import org.eclipse.lsp4j.*
import org.gls.groovy.GroovyIndexer
import org.gls.lang.ReferenceFinder
import org.gls.lang.VarUsage
import spock.lang.Specification
import static org.gls.util.TestUtil.uriList


class VariableSpec extends Specification {

    def "test VarRef indexing"() {
        ReferenceFinder finder = new ReferenceFinder()
        String path = "./src/test/test-files/2"

        GroovyIndexer indexer = new GroovyIndexer(uriList(path), finder)
        indexer.index()

        Set<VarUsage> usages = finder.storage.getVarUsages()
        VarUsage reference = usages.find { it.varName == 'theString' }

        expect:
        usages.size() == 2
        reference.definitionLineNumber == 3
    }

    def "Test find references"() {
        setup:
        ReferenceFinder finder = new ReferenceFinder()
        String dirPath = "src/test/test-files/6"

        ReferenceParams params = new ReferenceParams()
        Position position = new Position(3, 16)
        params.position = position

        String filePath = new File(dirPath + "/FindReference.groovy").getCanonicalPath()
        params.setTextDocument(new TextDocumentIdentifier(filePath))

        when:
        GroovyIndexer indexer = new GroovyIndexer(uriList(dirPath), finder)
        indexer.index()
        List<Location> references = finder.getReferences(params)


        then:
        references.size() == 2
        references.find { it.range.start.line == 6 } != null
        references.find { it.range.start.line == 7 } != null
    }

    def "Test find references2"() {
        setup:
        ReferenceFinder finder = new ReferenceFinder()
        String dirPath = "src/test/test-files/6"

        ReferenceParams params = new ReferenceParams()
        Position position = new Position(3, 11)
        params.position = position

        String filePath = new File(dirPath + "/FindReference2.groovy").getCanonicalPath()
        params.setTextDocument(new TextDocumentIdentifier(filePath))

        when:
        GroovyIndexer indexer = new GroovyIndexer(uriList(dirPath), finder)
        indexer.index()
        List<Location> references = finder.getReferences(params)


        then:
        references.size() == 1
        references.find { it.range.start.line == 7 } != null
    }


    def "Test method argument"() {
        given:
        ReferenceFinder finder = new ReferenceFinder()
        String dirPath = "src/test/test-files/7"

        TextDocumentPositionParams params = new TextDocumentPositionParams()
        Position position = new Position(12, 18)
        params.position = position

        String filePath = new File(dirPath + "/MethodArgument.groovy").getCanonicalPath()
        params.setTextDocument(new TextDocumentIdentifier(filePath))

        when:
        GroovyIndexer indexer = new GroovyIndexer(uriList(dirPath), finder)
        indexer.index()
        List<Location> definitions = finder.getDefinition(params)


        then:
        definitions.size() == 1
        definitions.first().range.start.line == 11
    }

}
