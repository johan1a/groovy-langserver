package org.gls

import org.eclipse.lsp4j.*
import org.gls.groovy.GroovyIndexer
import org.gls.lang.definition.ClassDefinition
import org.gls.lang.reference.ClassReference
import org.gls.lang.LanguageService
import org.gls.lang.reference.VarReference
import spock.lang.Specification
import spock.lang.Unroll

import static org.gls.util.TestUtil.uri

@Unroll
class ReferenceSpec extends Specification {

    def "test function return type"() {
        LanguageService finder = new LanguageService()
        String path = "src/test/test-files/3"

        GroovyIndexer indexer = new GroovyIndexer(uri(path), finder, new IndexerConfig())
        indexer.index()

        Set<ClassDefinition> definitions = finder.storage.getClassDefinitions().findAll {it.getFullClassName() == "Box"}
        def usages = finder.getClassReferences()
        ClassReference usage = usages.find{it.getFullReferencedClassName() == "Box" }

        expect:
        definitions.first().lineNumber == 0
        usage.lineNumber == 3
    }

    def "test Vardecl class usage"() {
        LanguageService finder = new LanguageService()
        String path = "src/test/test-files/4"

        GroovyIndexer indexer = new GroovyIndexer(uri(path), finder, new IndexerConfig())
        indexer.index()

        String testFilePath = new File(path + "/VarDeclClassUsage.groovy").getCanonicalPath()
        Set<ClassReference> usages = finder.getClassReferences()
        ClassReference usage = usages.find { it.fullReferencedClassName == "VarDeclClassUsage" }

        expect:
        usage.lineNumber == 7
    }

    def "Function reference 1"() {
        given:
        LanguageService finder = new LanguageService()
        String dirPath = "src/test/test-files/${_dir}"

        ReferenceParams params = new ReferenceParams()
        Position position = _pos
        params.position = position

        String filePath = new File(dirPath + "/${_class}.groovy").getCanonicalPath()
        params.setTextDocument(new TextDocumentIdentifier(filePath))

        when:
        GroovyIndexer indexer = new GroovyIndexer(uri(dirPath), finder, new IndexerConfig())
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
        10    | new Position(11, 17) | "FunctionReference"  | 8             | 15
    }

    def "Multiple function references 1"() {
        given:
        LanguageService finder = new LanguageService()
        String dirPath = "src/test/test-files/${_dir}"

        ReferenceParams params = new ReferenceParams()
        Position position = _pos
        params.position = position

        String filePath = new File(dirPath + "/${_class}.groovy").getCanonicalPath()
        params.setTextDocument(new TextDocumentIdentifier(filePath))

        when:
        GroovyIndexer indexer = new GroovyIndexer(uri(dirPath), finder, new IndexerConfig())
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
        'functions/two'     | new Position(64, 25)  | "ReferenceFinder"    |  1
        'functions/two'     | new Position(12, 21)  | "ReferenceStorage"   |  1
        'functions/two'     | new Position(61, 23)  | "ReferenceFinder"    |  1
        'definition/1'      | new Position(1, 6)    | "Constructor"        |  1
    }

    def "test VarRef indexing"() {
        LanguageService finder = new LanguageService()
        String path = "./src/test/test-files/2"

        GroovyIndexer indexer = new GroovyIndexer(uri(path), finder, new IndexerConfig())
        indexer.index()

        Set<VarReference> usages = finder.storage.getVarReferences()
        VarReference reference = usages.find { it.varName == 'theString' }

        expect:
        usages.size() == 2
        reference.definitionLineNumber == 3
    }

    def "Test find references"() {
        setup:
        LanguageService finder = new LanguageService()
        String dirPath = "src/test/test-files/6"

        ReferenceParams params = new ReferenceParams()
        Position position = new Position(3, 16)
        params.position = position

        String filePath = new File(dirPath + "/FindReference.groovy").getCanonicalPath()
        params.setTextDocument(new TextDocumentIdentifier(filePath))

        when:
        GroovyIndexer indexer = new GroovyIndexer(uri(dirPath), finder, new IndexerConfig())
        indexer.index()
        List<Location> references = finder.getReferences(params)

        then:
        references.size() == 2
        references.find { it.range.start.line == 6 } != null
        references.find { it.range.start.line == 7 } != null
    }

    def "Test find references2"() {
        setup:
        LanguageService finder = new LanguageService()
        String dirPath = "src/test/test-files/6"

        ReferenceParams params = new ReferenceParams()
        Position position = _position
        params.position = position

        String filePath = new File(dirPath + "/${_class}.groovy").getCanonicalPath()
        params.setTextDocument(new TextDocumentIdentifier(filePath))

        when:
        GroovyIndexer indexer = new GroovyIndexer(uri(dirPath), finder, new IndexerConfig())
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
