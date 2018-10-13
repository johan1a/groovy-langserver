package org.gls

import spock.lang.Specification

class ConfigServiceSpec extends Specification {

    void "It should set the config dir"() {
        given:
            ConfigService service = new ConfigService()

            String baseDir = System.getProperty("user.dir")
            URI rootDir = new URI("file://" + baseDir)
            String userHome = System.getProperty("user.home")
            String expected = "file://${userHome}/.langserver${baseDir}"

        when:
            URI configDir = service.getConfigDir(rootDir)

        then:
            configDir.toString() == expected
    }
}
