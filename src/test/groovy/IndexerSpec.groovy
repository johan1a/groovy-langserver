
import org.gls.groovy.GroovyIndexer
import org.gls.lang.ReferenceStorage
import spock.lang.Specification
import java.nio.file.Paths

class IndexerSpec extends Specification {
  def "test indexer"() {
    ReferenceStorage storage = new ReferenceStorage()
    String path = "./src/test/resources/test-files/"
    String url = Paths.get(path).toUri().toURL().toString();

    GroovyIndexer indexer = new GroovyIndexer(url, storage)
    indexer.startIndexing()

    expect:
    storage.classDefinitions.values().size() == 1

  }
}
