package org.gls

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.eclipse.lsp4j.*
import org.eclipse.lsp4j.jsonrpc.messages.Either
import org.eclipse.lsp4j.services.TextDocumentService
import org.gls.lang.ReferenceFinder
import org.eclipse.lsp4j.services.LanguageClientAware
import java.util.concurrent.CompletableFuture
import org.gls.groovy.GroovyIndexer
import org.codehaus.groovy.control.messages.*
import org.eclipse.lsp4j.services.LanguageClient
import org.codehaus.groovy.control.CompilationUnit
import org.codehaus.groovy.control.ErrorCollector
import org.codehaus.groovy.syntax.SyntaxException
import org.eclipse.lsp4j.*

@Slf4j
@TypeChecked
class GroovyTextDocumentService implements TextDocumentService, LanguageClientAware {

    private List<URI> sourcePaths
    private ReferenceFinder finder
    private LanguageClient client
    FileWatcher fileWacher = new FileWatcher()


    List<URI> getSourcePaths() {
        return sourcePaths
    }

    public void showClientMessage(String message) {
        client?.showMessage(new MessageParams(MessageType.Info, message))
    }

    public static void sendDiagnostics(Map<String, List<Diagnostic>> diagnostics, LanguageClient client) {
        diagnostics.keySet().each {
                PublishDiagnosticsParams params = new PublishDiagnosticsParams(it, diagnostics.get(it))
            client?.publishDiagnostics(params)
        }
    }

    @Override
    void connect(LanguageClient client) {
        log.info "Connected to client."
        this.client = client
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
        params.textDocument.uri = params.textDocument.uri.replace("file://", "")
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
            def references = finder.getReferences(params)
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
        params.textDocument.uri = params.textDocument.uri.replace("file://", "")
        fileWacher.didOpen(params)
    }

    @Override
    public void didChange(DidChangeTextDocumentParams params) {
        params.textDocument.uri = params.textDocument.uri.replace("file://", "")
        fileWacher.didChange(params)
    }

    @Override
    public void didClose(DidCloseTextDocumentParams params) {
        params.textDocument.uri = params.textDocument.uri.replace("file://", "")
        fileWacher.didClose(params)
    }

    @Override
    public void didSave(DidSaveTextDocumentParams params) {
        try {
            params.textDocument.uri = params.textDocument.uri.replace("file://", "")
            fileWacher.didSave(params)
            index(fileWacher.changedFiles)
        } catch (Exception e) {
            log.error("error", e)
        }
    }

    void index(Map<String, String> changedFiles = Collections.emptyMap()) {
        ReferenceFinder finder = new ReferenceFinder()
        GroovyIndexer indexer = new GroovyIndexer(sourcePaths, finder)
        Map<String, List<Diagnostic> > diagnostics = indexer.index(changedFiles)
        this.finder = finder
        sendDiagnostics(diagnostics, client)
    }

    void setSourcePaths(List<URI> sourcePaths) {
        this.sourcePaths = sourcePaths
    }
}
