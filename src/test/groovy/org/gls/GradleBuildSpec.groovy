package org.gls

import spock.lang.Specification

import static org.gls.util.TestUtil.uri

class GradleBuildSpec extends Specification{


    def "Parse simple jar name"() {
        given:
        String path = "src/test/test-files/config/build1.fakegradle"
        GradleBuild gradleBuild = new GradleBuild(uri(path))

        when:
        List<Dependency> names = gradleBuild.parseJarNames()

        then:
        names.size() == 1
        Dependency dependency = names.first()
        dependency.group == 'org.codehaus.groovy'
        dependency.name == 'groovy-all'
        dependency.version == '2.4.14'
    }
}
