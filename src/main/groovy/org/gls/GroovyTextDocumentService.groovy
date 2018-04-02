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

    private ReferenceFinder finder
    private List<File> changedFiles = new LinkedList<>()
    private LanguageClient client

    GroovyIndexer indexer

    void setReferenceStorage(ReferenceFinder finder) {
        this.finder = finder
    }

    void setIndexer(GroovyIndexer indexer) {
        this.indexer = indexer
        indexer.index()
        sendDiagnostics(indexer.getErrorCollector(), client)
    }

    private void showClientMessage(String message) {
        client?.showMessage(new MessageParams(MessageType.Info, message))
    }

    private void sendDiagnostics(ErrorCollector errorCollector, LanguageClient client) {
        log.info "Logging errors..."
        log.info("errorCollector: ${errorCollector}")
        try {
            if(errorCollector == null) {
                return
            }
            List<SyntaxErrorMessage> errors = errorCollector.getErrors()
            List<Message> warnings = errorCollector.getWarnings()
            log.info("errors: ${errors}")
            log.info("warnings: ${warnings}")
            Map<String, List<Diagnostic> > diagnosticMap = new HashMap<>()
            errors?.each {
                SyntaxException exception = it.getCause()
                String uri = "file://" + exception.getSourceLocator()
                Diagnostic diagnostic = asDiagnostic(exception)

                List<Diagnostic> diagnostics = diagnosticMap.get(uri)
                if(diagnostics == null) {
                    diagnostics = new LinkedList<>()
                    diagnosticMap.put(uri, diagnostics)
                }
                diagnostics.add(diagnostic)
            }
            warnings?.each {
                log.info it.toString()
                log.info "TODO implement warning diagnostics"
            }
            diagnosticMap.keySet().each {
                PublishDiagnosticsParams params = new PublishDiagnosticsParams(it, diagnosticMap.get(it))
                log.info("PARAMS: ${params}")
                log.info("client?: ${client}")
                client?.publishDiagnostics(params)
            }
        } catch (Exception e) {
            log.error("Error", e)
        }
    }

    private Diagnostic asDiagnostic(SyntaxException exception) {
        log.info "${exception.getMessage()}"
        int line = exception.getLine() - 1
        Position start = new Position(line, exception.getStartColumn())
        Position end = new Position(line, exception.getEndColumn())
        Range range = new Range(start, end)

        Diagnostic diagnostic = new Diagnostic(range, exception.getMessage())
        return diagnostic
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
        log.info "didOpen"
    }

    @Override
    public void didChange(DidChangeTextDocumentParams params) {
        changedFiles.add(new File(params.textDocument.uri))
        log.info "didChange"
    }

    @Override
    public void didClose(DidCloseTextDocumentParams params) {
        log.info "didClose"
    }

    @Override
    public void didSave(DidSaveTextDocumentParams params) {
        log.info "didSave ${params}"
    }

}
