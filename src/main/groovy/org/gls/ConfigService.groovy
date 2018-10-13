package org.gls

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.gls.exception.ConfigException

import java.nio.file.Files
import java.nio.file.Paths

@TypeChecked
@Slf4j
class ConfigService {
    BuildType buildType
    List<String> dependencies
    private static final URI CONFIG_BASE_DIR = new URI("file://${System.getProperty("user.home")}/.langserver")

    @SuppressWarnings('CatchException')
    List<String> getDependencies(URI rootUri, String configLocation) {
        try {
            buildType = buildType ?: getBuildType(rootUri, configLocation)

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
            log.error('Error when resolving dependencies', e)
            return []
        }
    }

    void saveDependenciesToFile(URI rootUri, List<String> dependencies) {
        URI dependenciesStorage = getDependenciesPath(rootUri)

        log.info("Saving dependencies to ${dependenciesStorage}")

        File dependenciesFile = new File(dependenciesStorage)
        dependenciesFile.createNewFile()
        dependenciesFile.text = ''
        dependenciesFile.withWriter { out ->
            dependencies.each { String line ->
                out.println(line)
            }
        }
    }

    private URI getDependenciesPath(URI rootUri) {
        URI configDir = getConfigDir(rootUri)
        createDirIfNotExists(configDir)
        log?.debug("rootURI: ${rootUri}")
        log?.debug("configURI: ${configDir}")
        URI dependenciesStorage = UriUtils.appendURI(configDir, '/dependencies')
        dependenciesStorage
    }

    URI getConfigDir(URI rootUri) {
        String stringPath
        if (rootUri.scheme) {
            stringPath = Paths.get(rootUri).toString()
        } else {
            stringPath = rootUri.toString()
        }
        UriUtils.appendURI(CONFIG_BASE_DIR, stringPath)
    }

    List<String> loadDependenciesFromFile(URI rootUri) {
        URI dependenciesPath = getDependenciesPath(rootUri)
        File dependenciesFile = new File(dependenciesPath)
        if (!dependenciesFile.exists() || !dependenciesFile.isFile()) {
            return []
        }
        dependenciesFile.readLines()
    }

    private void createDirIfNotExists(URI uri) {
        File storageDir = new File(uri)
        if (!storageDir.exists() && !storageDir.isDirectory()) {
            log.info("Creating directory ${uri}")
            storageDir.mkdir()
        }
    }

    BuildType getBuildType(URI rootUri, String configLocation) {
        log.info('Searching for gradle...')
        URI gradlePath = UriUtils.appendURI(rootUri, configLocation)
        if (Files.exists(Paths.get(gradlePath))) {
            log.info 'Found gradle.'
            return new GradleBuild(gradlePath)
        }
        throw new ConfigException('Build type not supported.')
    }
}
