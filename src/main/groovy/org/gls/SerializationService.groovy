package org.gls

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.json.JsonOutput
import groovy.util.logging.Slf4j
import org.gls.lang.LanguageService

/**
 * Created by johan on 5/26/18.
 */
@Slf4j
class SerializationService {

    static void serialize(URI directory, LanguageService languageService) {
        URI filePath = UriUtils.appendURI(directory, "/references")
        ObjectMapper mapper = new ObjectMapper()
        String output = JsonOutput.prettyPrint(mapper.writeValueAsString(languageService.storage))
        File file = new File(filePath)
        file.createNewFile()
        file.text = output
    }
}
