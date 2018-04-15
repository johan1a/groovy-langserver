package org.gls

import groovy.transform.TypeChecked

import java.nio.file.Files
import java.nio.file.Paths
import java.util.regex.Matcher

@TypeChecked
class GradleBuild implements BuildType {

    String gradleHome = "~/.gradle/"
    URI configPath

    public GradleBuild(URI configPath) {
        this.configPath = configPath
    }

    @Override
    List<String> resolveClassPath() {
        List<Dependency> dependencies = parseDependencies()
        List<String> classPath = new LinkedList<>()
        dependencies.collect { Dependency it ->
            findJarLocation(it)
        }.each {
            it.map { path ->
                classPath.add(path)
            }
        }
        classPath
    }

    Optional<String> findJarLocation(Dependency dependency) {
        String name = dependency.getJarFileName()
        def directory = new File(gradleHome)
        Optional<String> result = Optional.empty()
        if (directory.isDirectory()) {
            directory.eachFileRecurse { File file ->
                if (file.name == name) {
                    result = Optional.of(file.absolutePath)
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
        if (line.contains("compile") ||
                line.contains("testCompile") ||
                line.contains("testRuntime")) {
            if (line.contains("group")) {
                parseSplitJarName(line)
            } else {
                parseSimpleJarName(line)
            }
        } else {
            return Optional.empty()
        }
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
