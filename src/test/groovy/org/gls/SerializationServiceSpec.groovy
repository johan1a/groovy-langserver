package org.gls

import org.gls.lang.LanguageService
import org.gls.util.TestUtil
import spock.lang.Specification
import java.nio.file.Files
import java.nio.file.Paths

@SuppressWarnings(["LineLength", "TrailingWhitespace"])
class SerializationServiceSpec extends Specification {

    void "It should be able to serialize a LanguageService"() {
        given:
            String path = "/tmp/${UUID.randomUUID().toString()}"
            Files.createDirectory(Paths.get(path))
            URI rootUri = TestUtil.uri(path)
            LanguageService languageService = new LanguageService()
            SerializationService.serialize(rootUri, languageService)
            File file = new File("${path}/references")

        expect:
            file.text == """{
    "classDefinitions": [
        
    ],
    "classReferences": [
        
    ],
    "varDefinitions": [
        
    ],
    "varReferences": [
        
    ],
    "funcDefinitions": [
        
    ],
    "funcReferences": [
        
    ]
}"""
    }

}
