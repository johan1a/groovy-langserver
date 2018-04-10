package org.gls

import groovy.util.logging.Slf4j
import groovy.transform.TypeChecked
import org.eclipse.lsp4j.DidChangeConfigurationCapabilities
import org.eclipse.lsp4j.DidChangeTextDocumentParams
import org.eclipse.lsp4j.DidCloseTextDocumentParams
import org.eclipse.lsp4j.DidOpenTextDocumentParams
import org.eclipse.lsp4j.DidSaveTextDocumentParams
import org.eclipse.lsp4j.TextDocumentContentChangeEvent
import org.eclipse.lsp4j.TextEdit

@Slf4j
@TypeChecked
class FileWatcher {

    Map<String, String> changedFiles = new HashMap<>()

    void didSave(DidSaveTextDocumentParams params) {
        log.info("didSave: ")
        changedFiles.remove(params.textDocument.uri)
    }

    void didChange(DidChangeTextDocumentParams params) {
        log.info("didChange: ")
        // TODO every change
        changedFiles.put(params.textDocument.uri, params.contentChanges.first().text)
    }

    void didClose(DidCloseTextDocumentParams params) {
        log.info("didClose: ")
        changedFiles.remove(params.textDocument.uri)
    }

    void didOpen(DidOpenTextDocumentParams params) {
    }

    void didEdit(Map<String, List<TextEdit>> edits) {
        edits.keySet().each { changedFiles.remove(it) }
    }
}
