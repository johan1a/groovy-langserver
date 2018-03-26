import org.gls.groovy.GroovyIndexer
import org.gls.lang.ClassDefinition
import org.gls.lang.ClassUsage
import org.gls.lang.ReferenceStorage
import org.gls.lang.VarUsage
import spock.lang.Specification
import java.nio.file.Paths

class IndexerSpec extends Specification {
    def "test indexer"() {
        ReferenceStorage storage = new ReferenceStorage()
        String path = "./src/test/test-files/1"
        URI uri = Paths.get(path).toUri()

        GroovyIndexer indexer = new GroovyIndexer(uri, storage)
        indexer.indexRecursive()

        expect:
        storage.getClassDefinitions().values().size() == 1
    }

    def "test VarRef indexing"() {
        ReferenceStorage storage = new ReferenceStorage()
        String path = "./src/test/test-files/2"
        URI uri = Paths.get(path).toUri()

        GroovyIndexer indexer = new GroovyIndexer(uri, storage)
        indexer.indexRecursive()

        VarUsage reference = storage.getVarUsages().values().first().first()

        expect:
        reference.definitionLineNumber == 3
    }

    def "test function return type"() {
        ReferenceStorage storage = new ReferenceStorage()
        String path = "src/test/test-files/3"
        URI uri = Paths.get(path).toUri()

        GroovyIndexer indexer = new GroovyIndexer(uri, storage)
        indexer.indexRecursive()


        String testFilePath = new File(path + "/FunctionReturnType.groovy").getCanonicalPath()

        ClassDefinition definition = storage.getClassDefinitions().get("Box")
        def usages = storage.getClassUsages(testFilePath)
        ClassUsage usage = usages.first()

        expect:
        definition.lineNumber == 0
        usage.lineNumber == 3
    }
}