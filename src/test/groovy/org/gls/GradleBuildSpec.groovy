package org.gls

import spock.lang.Specification
import spock.lang.Unroll

import static org.gls.util.TestUtil.uri

@Unroll
class GradleBuildSpec extends Specification {


    def "Parse simple jar name"() {
        given:
        String path = "src/test/test-files/config/${_file}.fakegradle"
        GradleBuild gradleBuild = new GradleBuild(uri(path))

        when:
        List<Dependency> names = gradleBuild.parseJarNames()

        then:
        names.size() == 1
        Dependency dependency = names.first()
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

    }
}
