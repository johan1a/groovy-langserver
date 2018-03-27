import org.eclipse.lsp4j.Location
import org.eclipse.lsp4j.Position
import org.eclipse.lsp4j.ReferenceParams
import org.eclipse.lsp4j.TextDocumentIdentifier
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
        URI uri = Paths.get(path).toUri()

        GroovyIndexer indexer = new GroovyIndexer(uri, finder)
        indexer.indexRecursive()

        expect:
        finder.getClassDefinitions().values().size() == 1
    }

    def "test VarRef indexing"() {
        ReferenceFinder finder = new ReferenceFinder()
        String path = "./src/test/test-files/2"
        URI uri = Paths.get(path).toUri()

        GroovyIndexer indexer = new GroovyIndexer(uri, finder)
        indexer.indexRecursive()

        Set<VarUsage> usages = finder.getVarUsages().values().first()
        VarUsage reference = usages.find { it.varName == 'theString' }

        expect:
        usages.size() == 2
        reference.definitionLineNumber == 3
    }

    def "test function return type"() {
        ReferenceFinder finder = new ReferenceFinder()
        String path = "src/test/test-files/3"
        URI uri = Paths.get(path).toUri()

        GroovyIndexer indexer = new GroovyIndexer(uri, finder)
        indexer.indexRecursive()


        String testFilePath = new File(path + "/FunctionReturnType.groovy").getCanonicalPath()

        ClassDefinition definition = finder.getClassDefinitions().get("Box")
        def usages = finder.getClassUsages(testFilePath)
        ClassUsage usage = usages.first()

        expect:
        definition.lineNumber == 0
        usage.lineNumber == 3
    }

    def "test Vardecl class usage"() {
        ReferenceFinder finder = new ReferenceFinder()
        String path = "src/test/test-files/4"
        URI uri = Paths.get(path).toUri()

        GroovyIndexer indexer = new GroovyIndexer(uri, finder)
        indexer.indexRecursive()

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
            URI uri = Paths.get(path).toUri()

        when:
            GroovyIndexer indexer = new GroovyIndexer(uri, finder)
            indexer.indexRecursive()

        then:
            notThrown Exception
    }

    def "Test find references"() {
        setup:
            ReferenceFinder finder = new ReferenceFinder()
            String dirPath = "src/test/test-files/6"
            URI uri = Paths.get(dirPath).toUri()

            ReferenceParams params = new ReferenceParams()
            Position position = new Position(3, 16)
            params.position = position

            String filePath = new File(dirPath + "/FindReference.groovy").getCanonicalPath()
            params.setTextDocument(new TextDocumentIdentifier(filePath))

        when:
            GroovyIndexer indexer = new GroovyIndexer(uri, finder)
            indexer.indexRecursive()
            List<Location> references = finder.getReferences(params)


        then:
            references.size() == 2
            references.find{ it.range.start.line == 6 } != null
            references.find{ it.range.start.line == 7 } != null
    }

}
