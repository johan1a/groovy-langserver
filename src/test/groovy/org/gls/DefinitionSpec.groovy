package org.gls

import org.eclipse.lsp4j.*
import org.gls.groovy.GroovyIndexer
import org.gls.lang.ClassUsage
import org.gls.lang.ReferenceFinder
import spock.lang.Specification
import spock.lang.Unroll

import static org.gls.util.TestUtil.uriList

@Unroll
class DefinitionSpec extends Specification {


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
        Map<String, List<Diagnostic>> errors = indexer.index()
        List<Location> definitions = finder.getDefinition(params)


        then:
        errors.isEmpty()
        definitions.size() == 1
        definitions.first().range.start.line == 3
    }

    def "Class definition"() {
        given:
        ReferenceFinder finder = new ReferenceFinder()
        String dirPath = "src/test/test-files/9"

        TextDocumentPositionParams params = new TextDocumentPositionParams()
        Position position = new Position(4, 21)
        params.position = position

        String filePath = new File(dirPath + "/ClassDefinition1.groovy").getCanonicalPath()
        params.setTextDocument(new TextDocumentIdentifier(filePath))

        when:
        GroovyIndexer indexer = new GroovyIndexer(uriList(dirPath), finder)
        indexer.index()
        List<Location> definitions = finder.getDefinition(params)
        Set<ClassUsage> usages = finder.storage.getClassUsages()

        then:
        definitions.size() == 1
        definitions.first().range.start.line == 1
        usages.size() == 3
        ClassUsage usage = usages.find{ it.lineNumber == 4}
        usage.columnNumber == 8
        usage.lastColumnNumber == 23
    }

    def "Find definition"() {
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
        def errors = indexer.index()
        List<Location> definitions = finder.getDefinition(params)

        then:
        errors.isEmpty()
        definitions.size() == 1
        definitions.first().uri.startsWith("/")
        Range range = definitions.first().range
        range.start == _expected
        range.end.character == _end

        where:
        _dir              | _pos                    | _class               |  _expected             | _end
        "9"               | new Position(4, 36)     | "ClassDefinition1"   |  new Position(7, 21)   | 31
        'functions/two'   | new Position(72, 46)    | "ReferenceFinder"    |  new Position(142, 25) | 45
        'functions/two'   | new Position(12, 8)     | "ReferenceFinder"    |  new Position(12, 6)   | 21
        'functions/two'   | new Position(19, 8)     | "ReferenceFinder"    |  new Position(12, 21)  | 27
    }

    def "Repeated query"() {
        given:
        String dirPath = "src/test/test-files/functions/two"

        TextDocumentPositionParams params1 = new TextDocumentPositionParams()
        Position position1 = new Position(72, 46)
        params1.position = position1

        String filePath = new File(dirPath + "/ReferenceFinder.groovy").getCanonicalPath()
        params1.setTextDocument(new TextDocumentIdentifier(filePath))

        when:
        GroovyTextDocumentService service = new GroovyTextDocumentService()
        service.setSourcePaths(uriList(dirPath))
        service.index()
        ReferenceFinder finder = service.finder
        List<Location> definitions1 = finder.getDefinition(params1)

        then:
        definitions1.size() == 1
        definitions1.first().uri.startsWith("/")
        Range range1 = definitions1.first().range
        range1.start == new Position(142, 25)
        range1.end.character == 45

        when:
        ReferenceParams params2 = new ReferenceParams()
        params2.setTextDocument(new TextDocumentIdentifier(filePath))
        Position position2 = new Position(12, 25)
        params2.position = position2
        List<Location> definitions2 = finder.getReferences(params2)

        then:
        definitions1.size() == 1
        definitions1.first().uri.startsWith("/")
        Range range2 = definitions2.first().range
        range2.start == new Position(15, 15)
        range2.end.character == 21



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
        Map<String, List<Diagnostic>> errors = indexer.index()
        List<Location> definitions = finder.getDefinition(params)


        then:
        errors.isEmpty()
        definitions.size() == 1
        definitions.first().range.start.line == 11
    }



}
