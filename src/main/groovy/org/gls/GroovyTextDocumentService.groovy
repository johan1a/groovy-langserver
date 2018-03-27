package org.gls

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.eclipse.lsp4j.*
import org.eclipse.lsp4j.jsonrpc.messages.Either
import org.eclipse.lsp4j.services.TextDocumentService
import org.gls.lang.ReferenceFinder

import java.util.concurrent.CompletableFuture

@Slf4j
@TypeChecked
class GroovyTextDocumentService implements TextDocumentService {

    private ReferenceFinder finder

    void setReferenceStorage(ReferenceFinder finder) {
        this.finder = finder
    }

    @Override
    CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(TextDocumentPositionParams completionRequest) {
        log.info "completion"
    }

    @Override
    public CompletableFuture<CompletionItem> resolveCompletionItem(CompletionItem unresolved) {
        log.info "resolveCompletionItem"
    }

    @Override
    public CompletableFuture<Hover> hover(TextDocumentPositionParams position) {
        log.info "hover"
    }

    @Override
    public CompletableFuture<SignatureHelp> signatureHelp(TextDocumentPositionParams position) {
        log.info "signatureHelp"
    }

    @Override
    public CompletableFuture<List<? extends Location>> definition(TextDocumentPositionParams params) {
        try {
            log.info "definition: ${params}"
            def definition = finder.getDefinition(params)
            log.info "found definition: ${definition}"
            return CompletableFuture.completedFuture(definition)
        } catch (Exception e) {
            log.error("Exception", e)
            return CompletableFuture.completedFuture([])
        }
    }

    @Override
    public CompletableFuture<List<? extends Location>> references(ReferenceParams params) {
        log.info "references: ${params}"
        try {
            log.info "references: ${params}"
            def references = finder.getReferences(params)
            log.info "found references: ${references}"
            return CompletableFuture.completedFuture(references)
        } catch (Exception e) {
            log.error("Exception", e)
            return CompletableFuture.completedFuture([])
        }
    }

    @Override
    public CompletableFuture<List<? extends DocumentHighlight>> documentHighlight(TextDocumentPositionParams position) {
        log.info "documentHighlight"
        return CompletableFuture.completedFuture([])
    }

    @Override
    public CompletableFuture<List<? extends SymbolInformation>> documentSymbol(DocumentSymbolParams params) {
        log.info "documentSymbol"
        return CompletableFuture.completedFuture([])
    }

    @Override
    public CompletableFuture<List<? extends Command>> codeAction(CodeActionParams params) {
        log.info "codeAction"
        return CompletableFuture.completedFuture([])
    }

    @Override
    public CompletableFuture<List<? extends CodeLens>> codeLens(CodeLensParams params) {
        log.info "codeLens"
        return CompletableFuture.completedFuture([])
    }

    @Override
    public CompletableFuture<CodeLens> resolveCodeLens(CodeLens unresolved) {
        log.info "resolveCodeLens"
    }

    @Override
    public CompletableFuture<List<? extends TextEdit>> formatting(DocumentFormattingParams params) {
        log.info "formatting"
        return CompletableFuture.completedFuture([])
    }

    @Override
    public CompletableFuture<List<? extends TextEdit>> rangeFormatting(DocumentRangeFormattingParams params) {
        log.info "rangeFormatting"
        return CompletableFuture.completedFuture([])
    }

    @Override
    public CompletableFuture<List<? extends TextEdit>> onTypeFormatting(DocumentOnTypeFormattingParams params) {
        log.info "onTypeFormatting"
        return CompletableFuture.completedFuture([])
    }

    @Override
    public CompletableFuture<WorkspaceEdit> rename(RenameParams params) {
        log.info "rename"
    }

    @Override
    public void didOpen(DidOpenTextDocumentParams params) {
        log.info "didOpen"
    }

    @Override
    public void didChange(DidChangeTextDocumentParams params) {
        log.info "didChange"
    }

    @Override
    public void didClose(DidCloseTextDocumentParams params) {
        log.info "didClose"
    }

    @Override
    public void didSave(DidSaveTextDocumentParams params) {
        log.info "didSave"
    }

}
