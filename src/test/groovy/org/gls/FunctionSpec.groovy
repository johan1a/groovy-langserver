package org.gls

import org.eclipse.lsp4j.*
import org.gls.groovy.GroovyIndexer
import org.gls.lang.ReferenceFinder
import spock.lang.Specification
import spock.lang.Unroll

import static org.gls.util.TestUtil.uriList

@Unroll
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
        String dirPath = "src/test/test-files/${_dir}"

        TextDocumentPositionParams params = new TextDocumentPositionParams()
        Position position = _pos
        params.position = position

        String filePath = new File(dirPath + "/${_class}.groovy").getCanonicalPath()
        params.setTextDocument(new TextDocumentIdentifier(filePath))

        when:
        GroovyIndexer indexer = new GroovyIndexer(uriList(dirPath), finder)
        indexer.index()
        List<Location> definitions = finder.getDefinition(params)

        then:
        definitions.size() == 1

        Range range = definitions.first().range
        range.start == _expected

        where:
        _dir              | _pos                  | _class               |  _expected
        "9"               | new Position(4, 36)   | "ClassDefinition1"   |  new Position(7, 4)
        'functions/2'     | new Position(72, 46)  | "ReferenceFinder"    |  new Position(142, 4)
    }

    def "Function reference 1"() {
        given:
        ReferenceFinder finder = new ReferenceFinder()
        String dirPath = "src/test/test-files/${_dir}"

        ReferenceParams params = new ReferenceParams()
        Position position = _pos
        params.position = position

        String filePath = new File(dirPath + "/${_class}.groovy").getCanonicalPath()
        params.setTextDocument(new TextDocumentIdentifier(filePath))

        when:
        GroovyIndexer indexer = new GroovyIndexer(uriList(dirPath), finder)
        indexer.index()
        List<Location> definitions = finder.getReferences(params)

        then:
        definitions.size() == 1

        Range range = definitions.first().range
        range.start.line == _expectedLine
        range.start.character == _expectedChar
        where:
        _dir  | _pos                 | _class               | _expectedLine | _expectedChar
        9     | new Position(7, 28)  | "ClassDefinition1"   | 4             | 31
        10    | new Position(11, 17) | "FunctionReference" | 8             | 15
    }

    def "Multiple function references 1"() {
        given:
        ReferenceFinder finder = new ReferenceFinder()
        String dirPath = "src/test/test-files/${_dir}"

        ReferenceParams params = new ReferenceParams()
        Position position = _pos
        params.position = position

        String filePath = new File(dirPath + "/${_class}.groovy").getCanonicalPath()
        params.setTextDocument(new TextDocumentIdentifier(filePath))

        when:
        GroovyIndexer indexer = new GroovyIndexer(uriList(dirPath), finder)
        Map<String, List<Diagnostic>> errors = indexer.index()
        List<Location> references = finder.getReferences(params)

        then:
        errors.isEmpty()
        references.size() == _expected

        where:
        _dir              | _pos                  | _class               |  _expected
        'functions/1'     | new Position(16, 28)  | "MultipleFuncRefs1"  |  4
        'functions/2'     | new Position(64, 25)  | "ReferenceFinder"    |  1
        'functions/2'     | new Position(158, 49) | "ReferenceFinder"    |  3
        'functions/2'     | new Position(65, 25)  | "ReferenceFinder"    |  1
    }

}
