package org.gls

import spock.lang.Specification
import spock.lang.Unroll

import static org.gls.util.TestUtil.uri

@Unroll
class GradleBuildSpec extends Specification {


    def "Parse single jar name"() {
        given:
        String path = "src/test/test-files/config/${_file}.fakegradle"
        GradleBuild gradleBuild = new GradleBuild(uri(path))

        when:
        List<Dependency> dependencies = gradleBuild.parseDependencies()

        then:
        dependencies.size() == 1
        Dependency dependency = dependencies.first()
        dependency.group == _group
        dependency.name == _name
        dependency.version.present == (_version != null)
        if (dependency.version.present) {
            dependency.version.get() == _version
        }

        where:
        _file    | _group                | _name        | _version
        'build1' | 'org.codehaus.groovy' | 'groovy-all' | '2.4.14'
        'build2' | 'org.codehaus.groovy' | 'groovy-all' | '2.4.14'
        'build3' | 'org.codehaus.groovy' | 'groovy-all' | null
        'build4' | 'org.slf4j'           | 'slf4j-api'  | '1.7.25'
    }

    def "Parse multiple jar names"() {
        given:
        String path = "src/test/test-files/config/build5.fakegradle"
        GradleBuild gradleBuild = new GradleBuild(uri(path))

        when:
        List<Dependency> dependencies = gradleBuild.parseDependencies()

        then:
        dependencies.size() == 8
        dependencies*.name.containsAll([
                "groovy-all",
                "org.eclipse.lsp4j",
                "org.eclipse.lsp4j.jsonrpc",
                "slf4j-api",
                "slf4j-simple",
                "spock-core",
                "spock-reports",
                "junit"
        ])
    }

}
