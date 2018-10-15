package org.gls

import static org.gls.util.TestUtil.uri

import org.gls.groovy.GroovyCompilerService
import org.gls.lang.LanguageService
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class GradleBuildSpec extends Specification {
    private static final String GRADLE_HOME = "src/test/test-files/config/gradle_home"
    private static final String JAR_FILE_NAME = "/src/test/test-files/config/gradle_home/caches/modules-2/files-2.1/" +
            "org.slf4j/slf4j-api/1.7.25/962153db4a9ea71b79d047dfd1b2a0d80d8f4739/slf4j-api-1.7.25.jar"
    private static final String DOES_NOT_EXIST = "/does/not/exist"
    private static final String WORKDIR = System.getProperty("user.dir")

    void "find jar"() {
        given:
            String path = "src/test/test-files/config/build6.fakegradle"
            GradleBuild gradleBuild = new GradleBuild(uri(path))
            gradleBuild.gradleHome = GRADLE_HOME
            gradleBuild.libraries = [DOES_NOT_EXIST, gradleBuild.gradleHome]

        when:
            List<String> classPath = gradleBuild.resolveDependencies()

        then:
            classPath.size() == 1

            String expected = JAR_FILE_NAME
            classPath.first().split(WORKDIR)[1] == expected
    }

    void "find jar without specifying version"() {
        given:
            String path = "src/test/test-files/config/build7.fakegradle"
            GradleBuild gradleBuild = new GradleBuild(uri(path))
            gradleBuild.gradleHome = GRADLE_HOME

            gradleBuild.libraries = [DOES_NOT_EXIST, gradleBuild.gradleHome]

        when:
            List<String> classPath = gradleBuild.resolveDependencies()

        then:
            classPath.size() == 1

            String expected = JAR_FILE_NAME
            classPath.first().split(WORKDIR)[1] == expected
    }

    void "Make sure indexer classpath is updated"() {
        given:
            LanguageService finder = new LanguageService()
            String sourcePath = "src/test/test-files/config"

        when:
            IndexerConfig indexerConfig = new IndexerConfig(scanDependencies: true)
            GroovyCompilerService indexer = new GroovyCompilerService(uri(sourcePath), finder, indexerConfig)
            indexer.buildConfigLocation = "build7.fakegradle"
            indexer.compile()

        then:
            indexer.configService.buildType != null
    }

    void "Test dependency parsing"() {
        String path = "."
        GradleBuild gradleBuild = new GradleBuild(uri(path))

        when:
            List<Dependency> dependencies = gradleBuild.parseDependencies()

        then:
            dependencies.size() == 212
    }

}
