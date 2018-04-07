package org.gls

import org.gls.groovy.GroovyIndexer
import org.gls.lang.ReferenceFinder
import org.gls.util.TestUtil
import spock.lang.Specification
import static org.gls.util.TestUtil.uriList

class IndexerSpec extends Specification {

    def "test indexer"() {
        ReferenceFinder finder = new ReferenceFinder()
        String path = "./src/test/test-files/1"

        GroovyIndexer indexer = new GroovyIndexer(uriList(path), finder)
        indexer.index()

        expect:
        finder.storage.classDefinitions.size() == 1
    }

    def "Test unresolved import"() {
        setup:
        ReferenceFinder finder = new ReferenceFinder()
        String path = "src/test/test-files/5"

        when:
        GroovyIndexer indexer = new GroovyIndexer(TestUtil.uriList(path), finder)
        indexer.index()

        then:
        true // No exception was thrown
    }


}
