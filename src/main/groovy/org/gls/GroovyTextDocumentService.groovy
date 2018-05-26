package org.gls

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.eclipse.lsp4j.*
import org.eclipse.lsp4j.jsonrpc.messages.Either
import org.eclipse.lsp4j.services.LanguageClient
import org.eclipse.lsp4j.services.LanguageClientAware
import org.eclipse.lsp4j.services.TextDocumentService
import org.gls.groovy.GroovyCompilerService
import org.gls.lang.ImmutableLocation
import org.gls.lang.ImmutableRange
import org.gls.lang.LanguageService
import sun.reflect.generics.reflectiveObjects.NotImplementedException

import java.util.concurrent.CompletableFuture

@Slf4j
@TypeChecked
class GroovyTextDocumentService implements TextDocumentService, LanguageClientAware {

    private URI rootUri
    private LanguageService languageService = new LanguageService()
    private LanguageClient client
    private TextFileService textFileService = new TextFileService()
    private IndexerConfig indexerConfig = new IndexerConfig()

    public void showClientMessage(String message) {
        client?.showMessage(new MessageParams(MessageType.Info, message))
    }

    GroovyTextDocumentService(IndexerConfig config) {
        indexerConfig = config
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
    CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(TextDocumentPositionParams params) {
        long start = System.currentTimeMillis()
        //TODO reindex first when the file is changed client side?
        params.textDocument.uri = params.textDocument.uri.replace("file://", "")
        CompletableFuture<Either<List<CompletionItem>, CompletionList>> result = CompletableFuture.supplyAsync {
            List<CompletionItem> items
            try {
                log.info("completion params: ${params}")
                CompletionRequest request = textFileService.completionRequest(params)
                log.info("Got completionrequest: ${request}")
                if (!request.precedingText.contains(".")) {
                    log.info("Indexing before completing")
                    compile(textFileService.getChangedFiles())
                }
                items = languageService.getCompletionItems(request)
                def elapsed = (System.currentTimeMillis() - start) / 1000.0
                log.info("Completed in $elapsed ms")
                log.info("Returning: ${items}")
            } catch (Exception e) {
                log.error("Error when autocompleting", e)
                items = []
            }
            Either<List<CompletionItem>, CompletionList> left = Either.forLeft(items)
            left
        }

        return result
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
    public CompletableFuture<List<? extends ImmutableLocation>> definition(TextDocumentPositionParams params) {
        long start = System.currentTimeMillis()
        CompletableFuture<List<? extends ImmutableLocation>> result
        params.textDocument.uri = params.textDocument.uri.replace("file://", "")
        try {
            log.info "definition params: ${params}"
            def definition = externalURIs(languageService.getDefinition(params))
            log.info "found definition: ${definition}"
            result = CompletableFuture.completedFuture(definition)
        } catch (Exception e) {
            log.error("Exception", e)
            result = CompletableFuture.completedFuture([])
        }
        def elapsed = (System.currentTimeMillis() - start) / 1000.0
        log.info("Completed in $elapsed ms")
        return result
    }

    @Override
    public CompletableFuture<List<? extends ImmutableLocation>> references(ReferenceParams params) {
        long start = System.currentTimeMillis()
        CompletableFuture<List<? extends ImmutableLocation>> result
        params.textDocument.uri = params.textDocument.uri.replace("file://", "")
        log.info "reference params: ${params}"
        try {
            def references = externalURIs(languageService.getReferences(params))
            log.info "Found references: ${references}"
            result = CompletableFuture.completedFuture(references)
        } catch (Exception e) {
            log.error("Exception", e)
            result = CompletableFuture.completedFuture([])
        }
        def elapsed = (System.currentTimeMillis() - start) / 1000.0
        log.info("Completed in $elapsed ms")
        return result
    }

    static List<ImmutableLocation> externalURIs(List<ImmutableLocation> locations) {
        locations.collect { location ->
            Position start = new Position(location.range.start.line, location.range.start.character)
            Position end = new Position(location.range.end.line, location.range.end.character)
            return new ImmutableLocation("file://" + location.uri, new ImmutableRange(start, end))
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
        long start = System.currentTimeMillis()
        CompletableFuture<WorkspaceEdit> result
        params.textDocument.uri = params.textDocument.uri.replace("file://", "")
        log.info "rename params: ${params}"
        try {
            result = CompletableFuture.supplyAsync {
                Map<String, List<TextEdit>> edits = languageService.rename(params)
                log.info("edits: ${edits}")
                textFileService.changeFiles(edits)
                compile(textFileService.getChangedFiles())
                WorkspaceEdit edit = new WorkspaceEdit(externalUris(edits))
                edit
            }
        } catch (Exception e) {
            log.error("Exception", e)
            throw new NotImplementedException()
        }
        def elapsed = (System.currentTimeMillis() - start) / 1000.0
        log.info("Completed in $elapsed ms")
        return result
    }

    static Map<String, List<TextEdit>> externalUris(Map<String, List<TextEdit>> edits) {
        Map<String, List<TextEdit>> result = new HashMap<>()
        edits.entrySet().each {
            result.put("file://" + it, edits.get(it))
        }
        return result
    }

    @Override
    public void didOpen(DidOpenTextDocumentParams params) {
        params.textDocument.uri = params.textDocument.uri.replace("file://", "")
        textFileService.didOpen(params)
    }

    @Override
    public void didChange(DidChangeTextDocumentParams params) {
        params.textDocument.uri = params.textDocument.uri.replace("file://", "")
        textFileService.didChange(params)
    }

    @Override
    public void didClose(DidCloseTextDocumentParams params) {
        params.textDocument.uri = params.textDocument.uri.replace("file://", "")
        textFileService.didClose(params)
    }

    @Override
    public void didSave(DidSaveTextDocumentParams params) {
        try {
            params.textDocument.uri = params.textDocument.uri.replace("file://", "")
            textFileService.didSave(params)
            compile(textFileService.getChangedFiles())
        } catch (Exception e) {
            log.error("error", e)
        }
    }

    void compile(Map<String, String> changedFiles = Collections.emptyMap()) {
        try {
            LanguageService languageService = new LanguageService()
            GroovyCompilerService compilerService = new GroovyCompilerService(rootUri, languageService, indexerConfig)
            Map<String, List<Diagnostic>> diagnostics = compilerService.compile(changedFiles)
            indexerConfig.scanDependencies = false
            this.languageService = languageService
            sendDiagnostics(diagnostics, client)
        } catch (Exception e) {
            log.error("ERROR", e)
        }
    }

    void setRootUri(URI uri) {
        this.rootUri = uri
    }

}
