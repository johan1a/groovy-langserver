package org.gls

import org.eclipse.lsp4j.DidChangeTextDocumentParams
import org.eclipse.lsp4j.DidCloseTextDocumentParams
import org.eclipse.lsp4j.DidOpenTextDocumentParams
import org.eclipse.lsp4j.DidSaveTextDocumentParams
import org.eclipse.lsp4j.TextEdit
import org.gls.lang.FileWriterService

/**
 * Created by johan on 4/11/18.
 */
class FileService {

    private FileWatcher fileWatcher = new FileWatcher()
    private FileWriterService fileWriterService = new FileWriterService()

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
        fileWriterService.changeFiles(edits)
        fileWatcher.didEdit(edits)
    }
}

