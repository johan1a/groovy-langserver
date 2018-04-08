package org.gls

import org.eclipse.lsp4j.*
import org.gls.groovy.GroovyIndexer
import org.gls.lang.ClassDefinition
import org.gls.lang.ClassUsage
import org.gls.lang.ReferenceFinder
import org.gls.lang.VarUsage
import spock.lang.Specification
import spock.lang.Unroll

import static org.gls.util.TestUtil.uriList

@Unroll
class ReferenceSpec extends Specification {

    def "test function return type"() {
        ReferenceFinder finder = new ReferenceFinder()
        String path = "src/test/test-files/3"

        GroovyIndexer indexer = new GroovyIndexer(uriList(path), finder)
        indexer.index()

        String testFilePath = new File(path + "/FunctionReturnType.groovy").getCanonicalPath()

        ClassDefinition definition = finder.storage.getClassDefinitions().find{it.getFullClassName() == "Box"}
        def usages = finder.getClassUsages(testFilePath)
        ClassUsage usage = usages.first()

        expect:
        definition.lineNumber == 0
        usage.lineNumber == 3
    }

    def "test Vardecl class usage"() {
        ReferenceFinder finder = new ReferenceFinder()
        String path = "src/test/test-files/4"

        GroovyIndexer indexer = new GroovyIndexer(uriList(path), finder)
        indexer.index()

        String testFilePath = new File(path + "/VarDeclClassUsage.groovy").getCanonicalPath()
        Set<ClassUsage> usages = finder.getClassUsages(testFilePath)
        ClassUsage usage = usages.find { it.fullReferencedClassName == "VarDeclClassUsage" }

        expect:
        usage.lineNumber == 7
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
        _dir                | _pos                  | _class               |  _expected
        'functions/1'       | new Position(16, 23)  | "MultipleFuncRefs1"  |  4
        'functions/two'     | new Position(64, 25)  | "ReferenceFinder"    |  1
        'functions/two'     | new Position(158, 49) | "ReferenceFinder"    |  3
        'functions/two'     | new Position(65, 25)  | "ReferenceFinder"    |  1
    }

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
        Position position = _position
        params.position = position

        String filePath = new File(dirPath + "/${_class}.groovy").getCanonicalPath()
        params.setTextDocument(new TextDocumentIdentifier(filePath))

        when:
        GroovyIndexer indexer = new GroovyIndexer(uriList(dirPath), finder)
        indexer.index()
        List<Location> references = finder.getReferences(params)

        then:
        references.size() == 1
        references.find { it.range.start.line == _expectedLine } != null

        where:
        _class | _position | _expectedNbr | _expectedLine
        "FindReference2" | new Position(3, 11) | 1 | 7
        "FindReference3" | new Position(3, 11) | 1 | 8
    }


}
