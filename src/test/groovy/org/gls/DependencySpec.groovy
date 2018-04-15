package org.gls

import spock.lang.Specification

/**
 * Created by johan on 4/15/18.
 */
class DependencySpec extends Specification {

    def "test jar file name"() {
        given:
        Dependency dependency = new Dependency(
                group: _group,
                name: _name,
                version: Optional.ofNullable(_version)
        )

        expect:
        dependency.jarFileName == _expected

        where:
        _group | _name | _version | _expected
        "a"    | "b"   | "1.2"    | "b-1.2.jar"
        "a"    | "b"   | null     | "b.jar"

    }
}
