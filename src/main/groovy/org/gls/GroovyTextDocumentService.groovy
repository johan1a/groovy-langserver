package org.gls

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture
import org.eclipse.lsp4j.CodeActionParams
import org.eclipse.lsp4j.CodeLens
import org.eclipse.lsp4j.CodeLensParams
import org.eclipse.lsp4j.Command
import org.eclipse.lsp4j.CompletionItem
import org.eclipse.lsp4j.CompletionList
import org.eclipse.lsp4j.DidChangeTextDocumentParams
import org.eclipse.lsp4j.DidCloseTextDocumentParams
import org.eclipse.lsp4j.DidOpenTextDocumentParams
import org.eclipse.lsp4j.DidSaveTextDocumentParams
import org.eclipse.lsp4j.DocumentFormattingParams
import org.eclipse.lsp4j.DocumentHighlight
import org.eclipse.lsp4j.DocumentOnTypeFormattingParams
import org.eclipse.lsp4j.DocumentRangeFormattingParams
import org.eclipse.lsp4j.DocumentSymbolParams
import org.eclipse.lsp4j.Hover
import org.eclipse.lsp4j.Location
import org.eclipse.lsp4j.ReferenceParams
import org.eclipse.lsp4j.RenameParams
import org.eclipse.lsp4j.SignatureHelp
import org.eclipse.lsp4j.SymbolInformation
import org.eclipse.lsp4j.TextDocumentContentChangeEvent
import org.eclipse.lsp4j.TextDocumentItem
import org.eclipse.lsp4j.TextDocumentPositionParams
import org.eclipse.lsp4j.TextEdit
import org.eclipse.lsp4j.WorkspaceEdit
import org.eclipse.lsp4j.jsonrpc.messages.Either
import org.eclipse.lsp4j.services.TextDocumentService
import org.eclipse.lsp4j.DidChangeConfigurationParams
import org.eclipse.lsp4j.WorkspaceSymbolParams
import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j


@Slf4j
@TypeChecked
class GroovyTextDocumentService implements TextDocumentService {

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
    public CompletableFuture<List<? extends Location>> definition(TextDocumentPositionParams position) {
        log.info "definition"
        return CompletableFuture.completedFuture([])
    }

    @Override
    public CompletableFuture<List<? extends Location>> references(ReferenceParams params) {
        log.info "references: ${params}"
        return CompletableFuture.completedFuture([])
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
