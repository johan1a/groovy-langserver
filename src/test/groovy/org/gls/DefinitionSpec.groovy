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
        Range range = definitions.first().range
        range.start == _expected
        range.end.character == _end

        where:
        _dir              | _pos                    | _class               |  _expected             | _end
       // "9"               | new Position(4, 36)     | "ClassDefinition1"   |  new Position(7, 21)   | 31
        'functions/two'   | new Position(72, 46)    | "ReferenceFinder"    |  new Position(142, 25) | 45
        //'functions/two'   | new Position(72, 46)    | "ReferenceFinder"    |  new Position(142, 25) | 45
        //'functions/two'   | new Position(12, 8)     | "ReferenceFinder"    |  new Position(12, 6)   | 21
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
