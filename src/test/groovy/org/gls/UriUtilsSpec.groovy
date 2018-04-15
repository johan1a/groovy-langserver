package org.gls

import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class UriUtilsSpec extends Specification {

    void "test append uris"() {
        given:
        URI a = new URI(_first)
        String b = _second

        when:
        URI after = UriUtils.appendURI(a, b)

        then:
        after.toString() == _expected

        where:
        _first        | _second       | _expected
        "/first/one"  | "second/one"  | "/first/one/second/one"
        "/first/one"  | "/second/one" | "/first/one/second/one"
        "/first/one/" | "second/one"  | "/first/one/second/one"
        "/first/one/" | "/second/one" | "/first/one/second/one"

    }

}