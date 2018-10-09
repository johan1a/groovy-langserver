package org.gls

import groovy.transform.ToString

@ToString
class Dependency {

    String group
    String name
    String version

    String getJarFileName() {
        return "${name}-${version}.jar"
    }

}
