package org.gls

import org.gls.groovy.GroovyCompilerService
import org.gls.lang.LanguageService
import org.gls.util.TestUtil
import spock.lang.Specification

import static org.gls.util.TestUtil.uri

class IndexerSpec extends Specification {

    def "test indexer init"() {
        LanguageService finder = new LanguageService()
        def uri = TestUtil.uri(".")
        GroovyCompilerService indexer = new GroovyCompilerService(uri, finder, new IndexerConfig(scanAllSubDirs: false))

        expect:
        indexer.sourcePaths.collect { it.toString().split("groovy-langserver")[1] }
                .containsAll(["/./src/main/groovy", "/./grails-app/domain"])
    }

    def "test indexer"() {
        LanguageService finder = new LanguageService()
        String path = "./src/test/test-files/1"

        GroovyCompilerService indexer = new GroovyCompilerService(uri(path), finder, new IndexerConfig())
        indexer.compile()

        expect:
        finder.storage.classDefinitions.size() == 1
    }

    def "Test unresolved import"() {
        setup:
        LanguageService finder = new LanguageService()
        String path = "src/test/test-files/5"

        when:
        GroovyCompilerService indexer = new GroovyCompilerService(uri(path), finder, new IndexerConfig())
        indexer.compile()

        then:
        true // No exception was thrown
    }


}
