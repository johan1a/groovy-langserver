package org.gls

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j

import java.nio.file.Files
import java.nio.file.Paths

@TypeChecked
@Slf4j
class ConfigService {
    BuildType buildType
    List<String> dependencies
    private static final String CLASSPATH_STORAGE_LOCATION = ".langserver"

    List<String> getDependencies(URI rootUri, String configLocation) {
        try {
            if (!buildType) {
                buildType = getBuildType(rootUri, configLocation)
            }
            dependencies = loadDependenciesFromFile(rootUri)
            if (dependencies.isEmpty()) {
                dependencies = buildType.resolveDependencies()
                saveDependenciesToFile(rootUri, dependencies)
            }
            return dependencies
        } catch (Exception e) {
            log.error("Error when resolving dependencies", e)
            return []
        }
    }

    static void saveDependenciesToFile(URI rootUri, List<String> dependencies) {
        URI classpathStorageDir = UriUtils.appendURI(rootUri, CLASSPATH_STORAGE_LOCATION)
        createDirIfNotExists(classpathStorageDir)

        URI dependenciesStorage = UriUtils.appendURI(classpathStorageDir, "/dependencies")
        log.info("Saving dependencies to ${dependenciesStorage}")

        File dependenciesFile = new File(dependenciesStorage)
        dependenciesFile.createNewFile()
        dependenciesFile.text = ""
        dependenciesFile.withWriter { out ->
            dependencies.each { String line ->
                out.println(line)
            }
        }
    }

    static List<String> loadDependenciesFromFile(URI rootUri) {
        URI classpathStorageDir = UriUtils.appendURI(rootUri, CLASSPATH_STORAGE_LOCATION)
        createDirIfNotExists(classpathStorageDir)

        URI dependenciesStorage = UriUtils.appendURI(classpathStorageDir, "/dependencies")
        File dependenciesFile = new File(dependenciesStorage)
        if (!dependenciesFile.exists() || !dependenciesFile.isFile()) {
            return []
        }
        log.info("Loading dependencies from ${dependenciesStorage}")
        dependenciesFile.readLines()
    }

    private static void createDirIfNotExists(URI uri) {
        File storageDir = new File(uri)
        if (!storageDir.exists() && !storageDir.isDirectory()) {
            log.info("Creating directory ${uri}")
            boolean created = storageDir.mkdir()
            log.info("Dir created: ${created}")
        }
    }

    static BuildType getBuildType(URI rootUri, String configLocation) {
        log.info("Searching for gradle...")
        URI gradlePath = UriUtils.appendURI(rootUri, configLocation)
        log.info("Trying path: $gradlePath")
        if (Files.exists(Paths.get(gradlePath))) {
            log.info "Found gradle."
            return new GradleBuild(gradlePath)
        }
        throw new Exception("Build type not supported.")
    }
}
