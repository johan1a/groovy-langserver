package org.gls

class Dependency {

    String group
    String name
    String version

    String getJarFileName() {
        return "${name}-${version}.jar"
    }

    @Override
    public String toString() {
        return "Dependency{" +
                "group='" + group + '\'' +
                ", name='" + name + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
