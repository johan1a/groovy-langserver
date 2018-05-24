package org.gls

import org.gls.groovy.GroovyIndexer
import org.gls.lang.LanguageService
import spock.lang.Specification
import spock.lang.Unroll

import static org.gls.util.TestUtil.uri

@Unroll
class GradleBuildSpec extends Specification {

    def "find jar"() {
        given:
        String path = "src/test/test-files/config/build6.fakegradle"
        GradleBuild gradleBuild = new GradleBuild(uri(path))
        gradleBuild.gradleHome = "src/test/test-files/config/gradle_home"
        gradleBuild.libraries = ["/does/not/exist", gradleBuild.gradleHome]

        when:
        List<String> classPath = gradleBuild.resolveDependencies()

        then:
        classPath.size() == 1

        String expected = "/src/test/test-files/config/gradle_home/caches/modules-2/files-2.1/org.slf4j/slf4j-api/1.7.25/962153db4a9ea71b79d047dfd1b2a0d80d8f4739/slf4j-api-1.7.25.jar"
        classPath.first().split("groovy-langserver")[1] == expected
    }

    def "find jar without specifying version"() {
        given:
        String path = "src/test/test-files/config/build7.fakegradle"
        GradleBuild gradleBuild = new GradleBuild(uri(path))
        gradleBuild.gradleHome = "src/test/test-files/config/gradle_home"
        gradleBuild.libraries = ["/does/not/exist", gradleBuild.gradleHome]

        when:
        List<String> classPath = gradleBuild.resolveDependencies()

        then:
        classPath.size() == 1

        String expected = "/src/test/test-files/config/gradle_home/caches/modules-2/files-2.1/org.slf4j/slf4j-api/1.7.25/962153db4a9ea71b79d047dfd1b2a0d80d8f4739/slf4j-api-1.7.25.jar"
        classPath.first().split("groovy-langserver")[1] == expected
    }

    def "Make sure indexer classpath is updated"() {
        given:
        LanguageService finder = new LanguageService()
        String sourcePath = "src/test/test-files/config"

        when:
        GroovyIndexer indexer = new GroovyIndexer(uri(sourcePath), finder, new IndexerConfig(scanDependencies: true))
        indexer.buildConfigLocation = "build7.fakegradle"
        indexer.index()

        then:
        indexer.configService.buildType != null

    }

    def "Test dependency parsing"() {
        String path = "."
        GradleBuild gradleBuild = new GradleBuild(uri(path))

        when:
        List<Dependency> dependencies = gradleBuild.parseDependencies()

        then:
        dependencies.size() == 161
    }

}
