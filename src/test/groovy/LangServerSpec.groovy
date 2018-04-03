import groovy.transform.TypeChecked
import org.eclipse.lsp4j.InitializeParams
import org.gls.LangServer
import spock.lang.Specification

/**
 * Created by joha on 26-03-2018.
 */

@TypeChecked
class LangServerSpec extends Specification{
    def "test initialize"() {
        LangServer langServer = new LangServer()

        String uri = 'file:///test/uri'
        def params = new InitializeParams(rootUri: uri)
        langServer.initialize(params)

        URI uri1 = new URI("${uri}/src/main/groovy")
        URI uri2 = new URI("${uri}/grails-app")
        def expected = [uri1, uri2]
        expect:
        langServer.getIndexer().getRootUri() == expected
    }
}
