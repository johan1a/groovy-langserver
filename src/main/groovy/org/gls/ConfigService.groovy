package org.gls

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@TypeChecked
@Slf4j
class ConfigService {
    BuildType buildType
    List<String> dependencies
    private static final URI CLASSPATH_STORAGE_LOCATION = new URI("file://${System.getProperty("user.home")}/.langserver")

    List<String> getDependencies(URI rootUri, String configLocation) {
        try {
            if (!buildType) {
                buildType = getBuildType(rootUri, configLocation)
            }
            dependencies = loadDependenciesFromFile(rootUri)
            if (dependencies.isEmpty()) {
                dependencies = buildType.resolveDependencies()
                if (!dependencies.isEmpty()) {
                    saveDependenciesToFile(rootUri, dependencies)
                }
            }
            log.info("Found ${dependencies.size()} dependencies")
            return dependencies
        } catch (Exception e) {
            log.error("Error when resolving dependencies", e)
            return []
        }
    }

    static void saveDependenciesToFile(URI rootUri, List<String> dependencies) {
        URI classpathStorageDir = getConfigDir(rootUri)
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

    static URI getConfigDir(URI rootUri) {
        System.out.println("rootURI: ${rootUri}")

        String stringPath
        if (rootUri.scheme) {
            stringPath = Paths.get(rootUri).toString()
        } else {
            stringPath = rootUri.toString()
        }
        URI configUri = UriUtils.appendURI(CLASSPATH_STORAGE_LOCATION, stringPath)
        System.out.println("configURI: ${configUri}")
        Path path = Paths.get(configUri)
        if (!Files.isDirectory(path)) {
            File file = path.toFile()
            file.mkdirs()
            log.debug("Created config dir: ${configUri.toString()}")
        }
        log.debug("rootURI: ${rootUri}")
        log.debug("configURI: ${configUri}")
        configUri
    }

    static List<String> loadDependenciesFromFile(URI rootUri) {
        URI classpathStorageDir = UriUtils.appendURI(rootUri, CLASSPATH_STORAGE_LOCATION)
        createDirIfNotExists(classpathStorageDir)

        URI dependenciesStorage = UriUtils.appendURI(classpathStorageDir, "/dependencies")
        File dependenciesFile = new File(dependenciesStorage)
        if (!dependenciesFile.exists() || !dependenciesFile.isFile()) {
            return []
        }
        dependenciesFile.readLines()
    }

    private static void createDirIfNotExists(URI uri) {
        File storageDir = new File(uri)
        if (!storageDir.exists() && !storageDir.isDirectory()) {
            log.info("Creating directory ${uri}")
            storageDir.mkdir()
        }
    }

    static BuildType getBuildType(URI rootUri, String configLocation) {
        log.info("Searching for gradle...")
        URI gradlePath = UriUtils.appendURI(rootUri, configLocation)
        if (Files.exists(Paths.get(gradlePath))) {
            log.info "Found gradle."
            return new GradleBuild(gradlePath)
        }
        throw new Exception("Build type not supported.")
    }
}
