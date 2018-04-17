package org.gls

import org.gls.groovy.GroovyIndexer
import org.gls.lang.ReferenceFinder
import org.gls.util.TestUtil
import spock.lang.Specification

import static org.gls.util.TestUtil.uri

class IndexerSpec extends Specification {

    def "test indexer init"() {
        ReferenceFinder finder = new ReferenceFinder()
        def uri = TestUtil.uri(".")
        GroovyIndexer indexer = new GroovyIndexer(uri, finder, new IndexerConfig(scanAllSubDirs: false))

        expect:
        indexer.sourcePaths.collect { it.toString().split("groovy-langserver")[1] }
                .containsAll(["/./src/main/groovy", "/./grails-app"])
    }

    def "test indexer"() {
        ReferenceFinder finder = new ReferenceFinder()
        String path = "./src/test/test-files/1"

        GroovyIndexer indexer = new GroovyIndexer(uri(path), finder, new IndexerConfig())
        indexer.index()

        expect:
        finder.storage.classDefinitions.size() == 1
    }

    def "Test unresolved import"() {
        setup:
        ReferenceFinder finder = new ReferenceFinder()
        String path = "src/test/test-files/5"

        when:
        GroovyIndexer indexer = new GroovyIndexer(uri(path), finder, new IndexerConfig())
        indexer.index()

        then:
        true // No exception was thrown
    }


}
