import groovy.transform.TypeChecked
import org.gls.groovy.GroovyIndexer
import org.gls.lang.ReferenceStorage
import spock.lang.Specification
import java.nio.file.Paths

@TypeChecked
class IndexerSpec extends Specification {
  def "test indexer"() {
    ReferenceStorage storage = new ReferenceStorage()
    String path = "./src/test/test-files"
    URI uri = Paths.get(path).toUri()

    GroovyIndexer indexer = new GroovyIndexer(uri, storage)
    indexer.indexRecursive()

    expect:
    storage.getClassDefinitions().values().size() == 1
  }
}
