import org.eclipse.lsp4j.Location
import org.eclipse.lsp4j.Position
import org.eclipse.lsp4j.Range
import org.eclipse.lsp4j.ReferenceParams
import org.eclipse.lsp4j.TextDocumentIdentifier
import org.eclipse.lsp4j.TextDocumentPositionParams
import org.gls.groovy.GroovyIndexer
import org.gls.lang.ClassDefinition
import org.gls.lang.ClassUsage
import org.gls.lang.ReferenceFinder
import org.gls.lang.VarUsage
import spock.lang.Specification
import java.nio.file.Paths

class IndexerSpec extends Specification {

    def "test indexer"() {
        ReferenceFinder finder = new ReferenceFinder()
        String path = "./src/test/test-files/1"

        GroovyIndexer indexer = new GroovyIndexer(uriList(path), finder)
        indexer.index()

        expect:
        finder.storage.classDefinitions.values().size() == 1
    }

    def "test VarRef indexing"() {
        ReferenceFinder finder = new ReferenceFinder()
        String path = "./src/test/test-files/2"

        GroovyIndexer indexer = new GroovyIndexer(uriList(path), finder)
        indexer.index()

        Set<VarUsage> usages = finder.storage.varUsages.values().first()
        VarUsage reference = usages.find { it.varName == 'theString' }

        expect:
        usages.size() == 2
        reference.definitionLineNumber == 3
    }

    def "test function return type"() {
        ReferenceFinder finder = new ReferenceFinder()
        String path = "src/test/test-files/3"

        GroovyIndexer indexer = new GroovyIndexer(uriList(path), finder)
        indexer.index()


        String testFilePath = new File(path + "/FunctionReturnType.groovy").getCanonicalPath()

        ClassDefinition definition = finder.storage.classDefinitions.get("Box")
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
        ClassUsage usage = usages.find { it.referencedClassName == "VarDeclClassUsage" }

        expect:
        usage.lineNumber == 7
    }

    def "Test unresolved import"() {
        setup:
        ReferenceFinder finder = new ReferenceFinder()
        String path = "src/test/test-files/5"

        when:
        GroovyIndexer indexer = new GroovyIndexer(uriList(path), finder)
        indexer.index()

        then:
        true // No exception was thrown
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

    def "Func definition"() {
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

    def "Func definition 2"() {
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
        Set<ClassUsage> usages = finder.storage.getClassUsagesByFile(filePath)

        then:
        definitions.size() == 1
        definitions.first().range.start.line == 1
        usages.size() == 3
        ClassUsage usage = usages.find{ it.lineNumber == 4}
        usage.columnNumber == 8
        usage.lastColumnNumber == 23
    }

    private static List<URI> uriList(String path) {
        try {
            return [Paths.get(path).toUri()]
        } catch (Exception e) {
            return []
        }
    }

}
