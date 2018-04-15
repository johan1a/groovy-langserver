package org.gls

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.eclipse.lsp4j.*
import org.eclipse.lsp4j.jsonrpc.CompletableFutures
import org.eclipse.lsp4j.jsonrpc.messages.Either
import org.eclipse.lsp4j.services.LanguageClient
import org.eclipse.lsp4j.services.LanguageClientAware
import org.eclipse.lsp4j.services.TextDocumentService
import org.gls.groovy.GroovyIndexer
import org.gls.lang.ImmutableLocation
import org.gls.lang.ImmutableRange
import org.gls.lang.ReferenceFinder
import sun.reflect.generics.reflectiveObjects.NotImplementedException

import java.util.concurrent.CompletableFuture

@Slf4j
@TypeChecked
class GroovyTextDocumentService implements TextDocumentService, LanguageClientAware {

    private URI rootUri
    private ReferenceFinder finder = new ReferenceFinder()
    private LanguageClient client
    private FileService fileService = new FileService()
    private IndexerConfig indexerConfig = new IndexerConfig()

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
    CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(TextDocumentPositionParams params) {
        long start = System.currentTimeMillis()
        params.textDocument.uri = params.textDocument.uri.replace("file://", "")
        CompletableFuture<Either<List<CompletionItem>, CompletionList>> result
        CompletionList list = new CompletionList()

        CompletionItem item = new CompletionItem("testLabel")
        item.setKind(CompletionItemKind.Variable)
        list.items = Arrays.asList(
                item
        )
        result = CompletableFuture.completedFuture(Either.forRight(list))
        def elapsed = (System.currentTimeMillis() - start) / 1000.0
        log.info("Completed in $elapsed ms")
        log.info("Returning: ${result}")
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
            def definition = externalURIs(finder.getDefinition(params))
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
            def references = externalURIs(finder.getReferences(params))
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
                Map<String, List<TextEdit>> edits = finder.rename(params)
                log.info("edits: ${edits}")
                fileService.changeFiles(edits)
                index(fileService.getChangedFiles())
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
        fileService.didOpen(params)
    }

    @Override
    public void didChange(DidChangeTextDocumentParams params) {
        params.textDocument.uri = params.textDocument.uri.replace("file://", "")
        fileService.didChange(params)
    }

    @Override
    public void didClose(DidCloseTextDocumentParams params) {
        params.textDocument.uri = params.textDocument.uri.replace("file://", "")
        fileService.didClose(params)
    }

    @Override
    public void didSave(DidSaveTextDocumentParams params) {
        try {
            params.textDocument.uri = params.textDocument.uri.replace("file://", "")
            fileService.didSave(params)
            index(fileService.getChangedFiles())
        } catch (Exception e) {
            log.error("error", e)
        }
    }

    void index(Map<String, String> changedFiles = Collections.emptyMap()) {
        try {
            ReferenceFinder finder = new ReferenceFinder()
            GroovyIndexer indexer = new GroovyIndexer(rootUri, finder, indexerConfig.scanAllSubDirs)
            Map<String, List<Diagnostic>> diagnostics = indexer.index(changedFiles)
            this.finder = finder
            sendDiagnostics(diagnostics, client)
        } catch (Exception e) {
            log.error("ERROR", e)
        }
    }

    void setRootUri(URI uri) {
        this.rootUri = uri
    }

}
