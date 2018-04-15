package org.gls

import groovy.transform.TypeChecked

import java.nio.file.Files
import java.nio.file.Paths

@TypeChecked
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
        URI gradlePath = new URI(rootUri.toString() + "build.gradle")
        if(Files.exists(Paths.get(gradlePath))){
            return new GradleBuild(gradlePath)
        }
        throw new Exception("Build type not supported.")
    }
}
