package org.gls

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j

import java.nio.file.Files
import java.nio.file.Paths
import java.util.regex.Matcher

@TypeChecked
@Slf4j
class GradleBuild implements BuildType {

    String gradleHome = System.getProperty("user.home") + "/.gradle/"
    List<String> libraries = [gradleHome, '/usr/share/grails']
    URI configPath

    public GradleBuild(URI configPath) {
        this.configPath = configPath
    }

    @Override
    List<String> resolveClassPath() {
        try {
            log.info("Parsing jars from build.gradle...")
            List<Dependency> dependencies = parseDependencies()
            log.info("Parsed jars from build.gradle: ${dependencies*.name})}")
            List<String> classPath = findJarLocation(dependencies)
            log.info("Found jars: ${classPath}")
            classPath
        } catch (Exception e) {
            log.error("Error", e)
            Collections.emptyList()
        }
    }

    List<String> findJarLocation(List<Dependency> dependencies) {
        List<String> names = dependencies*.getJarFileName()
        List<String> result = new LinkedList<>()
        libraries.each { library ->
            File directory = new File(library)
            log.info("Searching for jars in: ${directory.absolutePath}")
            if (directory.isDirectory()) {
                directory.eachFileRecurse { File file ->
                    if (names.contains(file.name)) {
                        result.add(file.absolutePath)
                    }
                }
            }
        }
        return result
    }

    List<Dependency> parseDependencies() {
        List<String> configLines = Files.readAllLines(Paths.get(configPath))
        List<Dependency> dependencies = new LinkedList<>()

        configLines.collect { line ->
            parseJarName(line).map { dependencies.add(it) }
        }
        dependencies
    }

    static Optional<Dependency> parseJarName(String line) {
        try {
            if (!isComment(line) && (line.contains("compile") ||
                    line.contains("testCompile") ||
                    line.contains("testRuntime"))) {
                if (line.contains("group")) {
                    return parseSplitJarName(line)
                } else {
                    return parseSimpleJarName(line)
                }
            }
        } catch (Exception e) {
            log.error("Error while parsing $line", e)
        }
        return Optional.empty()
    }

    static boolean isComment(String line) {
        return line.trim().startsWith("//")
    }

    static Optional<Dependency> parseSplitJarName(String line) {
        String[] split = line.split(",")
        Dependency dependency = new Dependency(version: Optional.empty())
        split.each { String part ->
            parseVersionPart(dependency, part)
        }
        Optional.of(dependency)
    }

    static def parseVersionPart(Dependency dependency, String part) {
        Matcher matcher = (part =~ /.*['"](.*)['"].*/)
        matcher.find()
        if (part.contains("group")) {
            dependency.group = matcher.group(1)
        } else if (part.contains("name")) {
            dependency.name = matcher.group(1)
        } else if (part.contains("version")) {
            dependency.version = Optional.of(matcher.group(1))
        }
    }

    static Optional<Dependency> parseSimpleJarName(String line) {
        String group
        String name
        Optional<String> version

        Matcher matcher = (line =~ /.*['"](.*):(.*):(.*)['"]/)
        matcher.find()
        if (matcher.matches()) {
            group = matcher.group(1)
            name = matcher.group(2)
            version = Optional.of(matcher.group(3))
        } else {
            matcher = (line =~ /.*['"](.*):(.*)['"]/)
            matcher.find()
            group = matcher.group(1)
            name = matcher.group(2)
            version = Optional.empty()
        }
        return Optional.of(new Dependency(group: group, name: name, version: version))

    }
}
