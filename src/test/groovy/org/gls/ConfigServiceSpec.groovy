package org.gls

import spock.lang.Specification

class ConfigServiceSpec extends Specification {

    void "It should set the config dir"() {
        given:
            ConfigService service = new ConfigService()
            URI rootDir = new URI("file://" + System.getProperty("user.dir"))
            String expected = "file://${System.getProperty("user.home")}/.langserver/home/johan/dev/groovy-langserver"

        when:
            URI configDir = service.getConfigDir(rootDir)

        then:
            configDir.toString() == expected
    }
}
