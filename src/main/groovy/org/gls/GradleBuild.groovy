package org.gls

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j

@TypeChecked
@Slf4j
class GradleBuild implements BuildType {

    String gradleHome = System.getProperty("user.home") + "/.gradle/"
    String mavenHome = System.getProperty("user.home") + "/.m2/"
    List<String> libraries = [gradleHome, '/usr/share/grails', mavenHome]

    URI configPath

    GradleBuild(URI configPath) {
        this.configPath = configPath
    }

    @Override
    List<String> resolveDependencies() {
        try {
            log.info("Parsing jars from gradle")
            List<Dependency> dependencies = parseDependencies()
            log.debug("Parsed ${dependencies.size()} dependencies from gradle config)}")
            List<String> classPath = findJarLocation(dependencies)
            log.debug("Found ${classPath.size()} jars on filesystem")
            return classPath
        } catch (Exception e) {
            log.error("Error", e)
            Collections.emptyList()
        }
    }

    List<String> findJarLocation(List<Dependency> dependencies) {
        List<String> result = new LinkedList<>()
        libraries.each { library ->
            File directory = new File(library)
            log.info("Searching for jars in: ${directory.absolutePath}")
            if (directory.isDirectory()) {
                directory.eachFileRecurse { File file ->
                    if (jarNameMatch(file, dependencies)) {
                        result.add(file.absolutePath)
                    }
                }
            }
        }
        return result
    }

    static boolean jarNameMatch(File file, List<Dependency> dependencies) {
        String fileName = file.name
        if (file.isDirectory() ||
                !fileName.endsWith(".jar") ||
                fileName.endsWith("sources.jar") ||
                fileName.endsWith("javadoc.jar")) {
            return false
        }
        dependencies.any {
            fileName.contains(it.jarFileName)
        }
    }

    static List<Dependency> parseDependencies() {
        List<String> gradleOutput = callGradle()
        List<Dependency> dependencies = new LinkedList<>()

        gradleOutput.collect { line ->
            parseJarName(line).map { dependencies.add(it) }
        }
        dependencies.unique()
    }

    private static List<String> callGradle() {
        def sout = new StringBuilder(), serr = new StringBuilder()
        Process proc = './gradlew -q dependencies'.execute()
        proc.consumeProcessOutput(sout, serr)
        proc.waitForOrKill(10000)
        sout.toString().split(System.lineSeparator()).toList()
    }

    static Optional<Dependency> parseJarName(String line) {
        try {
            if (isDependencyLine(line)){
                return Optional.of(parseSplitJarName(line))
            }
        } catch (Exception e) {
            log.error("Error while parsing $line", e)
        }
        return Optional.empty()
    }

    static boolean isDependencyLine(String line) {
        return line.contains("+---") || line.contains("\\---")
    }

    static Dependency parseSplitJarName(String line) {
        String trimmed = trimLine(line)
        String[] split = trimmed.split(":")
        Dependency dependency = new Dependency(
                group: split[0],
                name: split[1],
                version: parseVersion(split[2])
        )
        return dependency
    }

    static String parseVersion(String version) {
        if(version.contains("->")){
            return version.split("->")[1].trim()
        }
        return version.trim()
    }

    static String trimLine(String line) {
        line.split("---")[1].replace("(*)", "").trim()
    }
}
