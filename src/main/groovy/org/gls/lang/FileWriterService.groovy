package org.gls.lang

import groovy.util.logging.Slf4j
import org.eclipse.lsp4j.TextEdit

/**
 * Created by johan on 4/10/18.
 */
@Slf4j
class FileWriterService {

    static def changeFiles(Map<String, List<TextEdit>> allEdits) {

        allEdits.each { fileName, edits ->
            log.info fileName

        }
    }
}
