package org.gls

import groovy.util.logging.Slf4j
import org.eclipse.lsp4j.DidChangeTextDocumentParams
import org.eclipse.lsp4j.DidCloseTextDocumentParams
import org.eclipse.lsp4j.DidOpenTextDocumentParams
import org.eclipse.lsp4j.DidSaveTextDocumentParams
import org.eclipse.lsp4j.TextEdit

@Slf4j
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

        allEdits.each { fileName, List<TextEdit> edits ->
            log.info fileName

            File file = new File(fileName)
            List<String> lines = file.readLines()
            edits.sort{ it.range.start.line }.each { doEdit(lines, it) }
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

