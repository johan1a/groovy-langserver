package org.gls

import static org.gls.util.TestUtil.uri

import org.gls.groovy.GroovyCompilerService
import org.gls.lang.LanguageService
import spock.lang.Specification

class IndexerSpec extends Specification {

    void "test indexer init"() {
        LanguageService finder = new LanguageService()
        URI uri = uri(".")

        GroovyCompilerService indexer = new GroovyCompilerService(uri, finder, new IndexerConfig(scanAllSubDirs: false))

        expect:
            indexer.sourcePaths.collect { it.toString().split(System.getProperty("user.dir"))[1] }
                    .containsAll(["/./src/main/groovy", "/./grails-app/domain"])
    }

    void "test indexer"() {
        LanguageService finder = new LanguageService()
        String path = "./src/test/test-files/1"

        GroovyCompilerService indexer = new GroovyCompilerService(uri(path), finder, new IndexerConfig())
        indexer.compile()

        expect:
            finder.storage.classDefinitions.size() == 1
    }

    void "Test unresolved import"() {
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
