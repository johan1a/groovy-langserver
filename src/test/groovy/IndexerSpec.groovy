import groovy.transform.TypeChecked
import org.gls.groovy.GroovyIndexer
import org.gls.lang.ReferenceStorage
import org.gls.lang.VarReference
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

        VarReference reference = storage.getVarReferences().values().first().first()

        expect:
        reference.definitionLineNumber == 3

    }
}
