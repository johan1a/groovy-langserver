package org.gls

import groovy.transform.TypeChecked

import java.nio.file.Files
import java.nio.file.Paths
import java.util.regex.MatchResult
import java.util.regex.Matcher

@TypeChecked
class GradleBuild implements BuildType {

    URI configPath

    public GradleBuild(URI configPath) {
        this.configPath = configPath
    }

    @Override
    List<String> resolveClassPath() {
        List<Dependency> names = parseJarNames()
        // TODO find on disk
        return null
    }

    List<Dependency> parseJarNames() {
        List<String> configLines = Files.readAllLines(Paths.get(configPath))
        List<Dependency> dependencies = new LinkedList<>()

        configLines.collect { line ->
            parseJarName(line).map { dependencies.add(it) }
        }
        dependencies
    }

    Optional<Dependency> parseJarName(String line) {
        if (line.contains("compile") || line.contains("testCompile")) {
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
    }

    static Optional<Dependency> parseSimpleJarName(String line) {
        Matcher matcher = (line =~ /.*['"](.*):(.*):(.*)['"]/)

        matcher.find()
        String group = matcher.group(1)
        String name = matcher.group(2)
        String version = matcher.group(3)
        return Optional.of(new Dependency(group: group, name: name, version: version))

    }
}
