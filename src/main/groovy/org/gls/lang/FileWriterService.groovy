package org.gls.lang

import groovy.util.logging.Slf4j
import org.codehaus.groovy.control.io.FileReaderSource
import org.eclipse.lsp4j.TextEdit

import java.nio.file.Files
import java.nio.file.Paths

/**
 * Created by johan on 4/10/18.
 */
@Slf4j
class FileWriterService {

    static void changeFiles(Map<String, List<TextEdit>> allEdits) {

        allEdits.each { fileName, List<TextEdit> edits ->
            log.info fileName

            File file = new File(fileName)
            List<String> lines = file.readLines()
            edits.each { doEdit(lines, it) }
            log.info("$lines")
            file.text = lines.join(System.lineSeparator())
        }
    }

    static void doEdit(List<String> fileLines, TextEdit textEdit) {
        int lineNbr = textEdit.range.start.line
        int characterNbr = textEdit.range.start.character
        int lastCharacterNbr = textEdit.range.end.character
        int originalSize = lastCharacterNbr - characterNbr
        String newText = textEdit.newText
        String line = fileLines.get(lineNbr)
        String pre = line.substring(0, characterNbr)
        String post = line.substring(characterNbr + originalSize)
        fileLines[lineNbr] = pre + newText + post
    }
}