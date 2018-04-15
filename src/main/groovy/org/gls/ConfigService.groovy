package org.gls

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j

import java.nio.file.Files
import java.nio.file.Paths

@TypeChecked
@Slf4j
class ConfigService {
    BuildType buildType

    List<String> resolveClassPath(URI rootUri) {
        try {
            buildType = getBuildType(rootUri)
            return buildType.resolveClassPath()
        } catch (Exception e){
            return []
        }
    }

    static BuildType getBuildType(URI rootUri) {
        log.info("Searching for gradle...")
        URI gradlePath = UriUtils.appendURI(rootUri, "build.gradle")
        if(Files.exists(Paths.get(gradlePath))){
            log.info "Found gradle."
            return new GradleBuild(gradlePath)
        }
        throw new Exception("Build type not supported.")
    }
}
