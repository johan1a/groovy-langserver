package org.gls

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.eclipse.lsp4j.DidChangeTextDocumentParams
import org.eclipse.lsp4j.DidCloseTextDocumentParams
import org.eclipse.lsp4j.DidOpenTextDocumentParams
import org.eclipse.lsp4j.DidSaveTextDocumentParams
import org.eclipse.lsp4j.TextDocumentPositionParams
import org.eclipse.lsp4j.TextEdit
import org.gls.lang.ImmutableLocation
import org.gls.lang.ImmutablePosition

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
            List<Integer> offsetByLine = [0] * lines.size()
            edits.sort{ a, b ->
                    if(a.range.start.line == b.range.start.line) {
                        a.range.start.character <=> b.range.start.character
                    } else {
                        a.range.start.line <=> b.range.start.line
                    }
                }.each { doEdit(lines, offsetByLine, it) }
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

    CompletionRequest completionRequest(TextDocumentPositionParams params) {
        String uri = params.textDocument.uri
        List<String> fileLines = readFileLines(uri)

        String precedingText = fileLines[params.position.line].substring(0, params.position.character)
        ImmutablePosition position = new ImmutablePosition(params.position.line, params.position.character - 2)

        return new CompletionRequest(uri: uri, position: position, precedingText: precedingText)
    }

    private List<String> readFileLines(String fileName) {
        if(getChangedFiles().containsKey(fileName)){
            return getChangedFiles().get(fileName).split(System.lineSeparator()).toList()
        }
        File file = new File(fileName)
        return file.readLines()
    }

    static void doEdit(List<String> fileLines, List<Integer> offsetByLine, TextEdit textEdit) {
        int lineNbr = textEdit.range.start.line
        int offset = offsetByLine[lineNbr]

        String line = fileLines.get(lineNbr)
        int start = textEdit.range.start.character + offset
        int end = textEdit.range.end.character + offset
        int oldTextSize = end - start + 1
        String post = line.substring(start + oldTextSize)
        String pre = line.substring(0, start)
        fileLines[lineNbr] = pre + textEdit.newText + post

        offsetByLine[lineNbr] += textEdit.newText.size() - oldTextSize
    }

}
