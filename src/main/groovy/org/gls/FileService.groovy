package org.gls

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.eclipse.lsp4j.DidChangeTextDocumentParams
import org.eclipse.lsp4j.DidCloseTextDocumentParams
import org.eclipse.lsp4j.DidOpenTextDocumentParams
import org.eclipse.lsp4j.DidSaveTextDocumentParams
import org.eclipse.lsp4j.TextEdit

@Slf4j
@TypeChecked
class FileService {

    private FileWatcher fileWatcher = new FileWatcher()

    Map<String, String> getChangedFiles() {
        return fileWatcher.changedFiles
    }

    void didSave(DidSaveTextDocumentParams didSaveTextDocumentParams) {
        fileWatcher.didSave(didSaveTextDocumentParams)
    }

    void didClose(DidCloseTextDocumentParams didCloseTextDocumentParams) {
        fileWatcher.didClose(didCloseTextDocumentParams)
    }

    void didChange(DidChangeTextDocumentParams didChangeTextDocumentParams) {
        fileWatcher.didChange(didChangeTextDocumentParams)
    }

    void didOpen(DidOpenTextDocumentParams didOpenTextDocumentParams) {
        fileWatcher.didOpen(didOpenTextDocumentParams)
    }

    void changeFiles(Map<String, List<TextEdit>> edits) {
        writeFiles(edits)
        fileWatcher.didEdit(edits)
    }

    void writeFiles(Map<String, List<TextEdit>> allEdits) {

        allEdits.each { String fileName, List<TextEdit> edits ->
            log.info fileName

            List<String> lines = readFileLines(fileName)
            edits.sort{ it.range.start.line }.each { doEdit(lines, it) }
            log.info("$lines")
            writeToFile(fileName, lines)
        }
    }

    void writeToFile(String fileName, List<String> lines) {
        File file = new File(fileName)
        file.text = lines.join(System.lineSeparator())
        if(getChangedFiles().containsKey(fileName)){
            getChangedFiles().remove(fileName)
        }
    }

    private List<String> readFileLines(String fileName) {
        if(getChangedFiles().containsKey(fileName)){
            return getChangedFiles().get(fileName).split(System.lineSeparator()).toList()
        }
        File file = new File(fileName)
        return file.readLines()
    }

    static void doEdit(List<String> fileLines, TextEdit textEdit) {
        int lineNbr = textEdit.range.start.line
        log.info("lineNbr: ${lineNbr}")
        int characterNbr = textEdit.range.start.character
        int lastCharacterNbr = textEdit.range.end.character
        int originalSize = lastCharacterNbr - characterNbr
        String newText = textEdit.newText
        String line = fileLines.get(lineNbr)
        String pre = line.substring(0, characterNbr)
        String post = line.substring(characterNbr + originalSize + 1)
        log.info("fileLines[lineNbr]: ${fileLines[lineNbr]}")
        fileLines[lineNbr] = pre + newText + post
        log.info("post: ${post}")
        log.info("newText: ${newText}")
        log.info("pre: ${pre}")
        log.info("fileLines[lineNbr]: ${fileLines[lineNbr]}")
    }

}
