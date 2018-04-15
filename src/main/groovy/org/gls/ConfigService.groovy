package org.gls

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j

import java.nio.file.Files
import java.nio.file.Paths

@TypeChecked
@Slf4j
class ConfigService {
    BuildType buildType

    List<String> resolveClassPath(URI rootUri, String configLocation) {
        try {
            buildType = getBuildType(rootUri, configLocation)
            return buildType.resolveClassPath()
        } catch (Exception e){
            log.error("Error: Build type not found.")
            return []
        }
    }

    static BuildType getBuildType(URI rootUri, String configLocation) {
        log.info("Searching for gradle...")
        URI gradlePath = UriUtils.appendURI(rootUri, configLocation)
        log.info("Trying path: $gradlePath")
        if(Files.exists(Paths.get(gradlePath))){
            log.info "Found gradle."
            return new GradleBuild(gradlePath)
        }
        throw new Exception("Build type not supported.")
    }
}
