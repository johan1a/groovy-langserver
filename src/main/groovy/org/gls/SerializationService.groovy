package org.gls

import groovy.json.JsonOutput
import groovy.util.logging.Slf4j
import org.gls.lang.LanguageService

/**
 * Created by johan on 5/26/18.
 */
@Slf4j
class SerializationService {

    static void serialize(URI rootUri, LanguageService languageService) {
        URI filePath = UriUtils.appendURI(rootUri, ".langserver/references")
        String output = JsonOutput.prettyPrint(JsonOutput.toJson(languageService))
        File dependenciesFile = new File(filePath)
        dependenciesFile.createNewFile()
        dependenciesFile.text = output
    }
}
