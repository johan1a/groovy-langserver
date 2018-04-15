package org.gls

/**
 * Created by johan on 4/15/18.
 */
class Dependency {

    String group
    String name
    Optional<String> version

    String getJarFileName(){
        return "${name}-${version.get()}.jar"
    }
}
