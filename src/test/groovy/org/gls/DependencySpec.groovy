package org.gls

import spock.lang.Specification

class DependencySpec extends Specification {

    def "test jar file name"() {
        given:
        Dependency dependency = new Dependency(
                group: _group,
                name: _name,
                version: _version
        )

        expect:
        dependency.jarFileName == _expected

        where:
        _group | _name | _version | _expected
        "a"    | "b"   | "1.2"    | "b-1.2.jar"

    }
}
