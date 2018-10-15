package org.gls

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.eclipse.lsp4j.CodeActionParams
import org.eclipse.lsp4j.CodeLens
import org.eclipse.lsp4j.CodeLensParams
import org.eclipse.lsp4j.Command
import org.eclipse.lsp4j.CompletionItem
import org.eclipse.lsp4j.CompletionList
import org.eclipse.lsp4j.Diagnostic
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
import org.eclipse.lsp4j.MessageParams
import org.eclipse.lsp4j.MessageType
import org.eclipse.lsp4j.Position
import org.eclipse.lsp4j.PublishDiagnosticsParams
import org.eclipse.lsp4j.ReferenceParams
import org.eclipse.lsp4j.RenameParams
import org.eclipse.lsp4j.SignatureHelp
import org.eclipse.lsp4j.SymbolInformation
import org.eclipse.lsp4j.TextDocumentPositionParams
import org.eclipse.lsp4j.TextEdit
import org.eclipse.lsp4j.WorkspaceEdit
import org.eclipse.lsp4j.jsonrpc.messages.Either
import org.eclipse.lsp4j.services.LanguageClient
import org.eclipse.lsp4j.services.LanguageClientAware
import org.eclipse.lsp4j.services.TextDocumentService
import org.gls.exception.NotImplementedException
import org.gls.groovy.GroovyCompilerService
import org.gls.lang.ImmutableLocation
import org.gls.lang.ImmutableRange
import org.gls.lang.LanguageService

import java.util.concurrent.CompletableFuture

@Slf4j
@TypeChecked
@SuppressWarnings(['CatchException'])
class GroovyTextDocumentService implements TextDocumentService, LanguageClientAware {

    private static final String FILE = "file://"

    private static final BigDecimal MILLIS_PER_SECOND = 1000.0

    private static final String GENERAL_EXCEPTION = "Exception"

    private URI rootUri
    private LanguageService languageService = new LanguageService()
    private LanguageClient client
    private final TextFileService textFileService = new TextFileService()
    private final IndexerConfig indexerConfig = new IndexerConfig()
    private final SerializationService serializationService = new SerializationService()

    void showClientMessage(String message) {
        client?.showMessage(new MessageParams(MessageType.Info, message))
    }

    GroovyTextDocumentService(IndexerConfig config) {
        indexerConfig = config
    }

    static void sendDiagnostics(Map<String, List<Diagnostic>> diagnostics, LanguageClient client) {
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
        params.textDocument.uri = params.textDocument.uri.replace(FILE, "")
        CompletableFuture<Either<List<CompletionItem>, CompletionList>> result = CompletableFuture.supplyAsync {
            List<CompletionItem> items
            try {
                log.info("Got completion request")
                log.debug("params: ${params}")
                CompletionRequest request = textFileService.completionRequest(params)
                log.debug("Got completionrequest: ${request}")
                if (!request.precedingText.contains(".")) {
                    log.debug("Indexing before completing")
                    compile(textFileService.changedFiles)
                }
                items = languageService.getCompletionItems(request)
                BigDecimal elapsed = (System.currentTimeMillis() - start) / MILLIS_PER_SECOND
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
    CompletableFuture<CompletionItem> resolveCompletionItem(CompletionItem unresolved) {
        log.info "resolveCompletionItem"
    }

    @Override
    CompletableFuture<Hover> hover(TextDocumentPositionParams position) {
        log.info "hover"
    }

    @Override
    CompletableFuture<SignatureHelp> signatureHelp(TextDocumentPositionParams position) {
        log.info "signatureHelp"
    }

    @Override
    CompletableFuture<List<? extends ImmutableLocation>> definition(TextDocumentPositionParams params) {
        long start = System.currentTimeMillis()
        CompletableFuture<List<? extends ImmutableLocation>> result
        params.textDocument.uri = params.textDocument.uri.replace(FILE, "")
        try {
            log.info "got definition request"
            log.debug "params: ${params}"
            result = CompletableFuture.supplyAsync {
                List<ImmutableLocation> definition = externalURIs(languageService.getDefinition(params))
                log.debug "Found definition: ${!definition.isEmpty()}"
                log.debug "definition: ${definition}"
                definition
            }
        } catch (Exception e) {
            log.error(GENERAL_EXCEPTION, e)
            result = CompletableFuture.completedFuture([])
        }
        BigDecimal elapsed = (System.currentTimeMillis() - start) / MILLIS_PER_SECOND
        log.info("Completed in $elapsed ms")
        return result
    }

    @Override
    CompletableFuture<List<? extends ImmutableLocation>> references(ReferenceParams params) {
        long start = System.currentTimeMillis()
        CompletableFuture<List<? extends ImmutableLocation>> result
        params.textDocument.uri = params.textDocument.uri.replace(FILE, "")
        log.info "got references request"
        log.debug "params: ${params}"
        try {
            List<ImmutableLocation> references = externalURIs(languageService.getReferences(params))
            log.debug "Found ${references.size()} references"
            log.debug "references: ${references}"
            result = CompletableFuture.completedFuture(references)
        } catch (Exception e) {
            log.error(GENERAL_EXCEPTION, e)
            result = CompletableFuture.completedFuture([])
        }
        BigDecimal elapsed = (System.currentTimeMillis() - start) / MILLIS_PER_SECOND
        log.info("Completed in $elapsed ms")
        return result
    }

    static List<ImmutableLocation> externalURIs(List<ImmutableLocation> locations) {
        locations.collect { location ->
            Position start = new Position(location.range.start.line, location.range.start.character)
            Position end = new Position(location.range.end.line, location.range.end.character)
            return new ImmutableLocation(FILE + location.uri, new ImmutableRange(start, end))
        }
    }

    @Override
    CompletableFuture<List<? extends DocumentHighlight>> documentHighlight(TextDocumentPositionParams position) {
        log.info "documentHighlight"
        return CompletableFuture.completedFuture([])
    }

    @Override
    CompletableFuture<List<? extends SymbolInformation>> documentSymbol(DocumentSymbolParams params) {
        log.info "documentSymbol"
        return CompletableFuture.completedFuture([])
    }

    @Override
    CompletableFuture<List<? extends Command>> codeAction(CodeActionParams params) {
        log.info "codeAction"
        return CompletableFuture.completedFuture([])
    }

    @Override
    CompletableFuture<List<? extends CodeLens>> codeLens(CodeLensParams params) {
        log.info "codeLens"
        return CompletableFuture.completedFuture([])
    }

    @Override
    CompletableFuture<CodeLens> resolveCodeLens(CodeLens unresolved) {
        log.info "resolveCodeLens"
    }

    @Override
    CompletableFuture<List<? extends TextEdit>> formatting(DocumentFormattingParams params) {
        log.info "formatting"
        return CompletableFuture.completedFuture([])
    }

    @Override
    CompletableFuture<List<? extends TextEdit>> rangeFormatting(DocumentRangeFormattingParams params) {
        log.info "rangeFormatting"
        return CompletableFuture.completedFuture([])
    }

    @Override
    CompletableFuture<List<? extends TextEdit>> onTypeFormatting(DocumentOnTypeFormattingParams params) {
        log.info "onTypeFormatting"
        return CompletableFuture.completedFuture([])
    }

    @Override
    CompletableFuture<WorkspaceEdit> rename(RenameParams params) {
        log.info "Got rename request"
        long start = System.currentTimeMillis()
        CompletableFuture<WorkspaceEdit> result
        params.textDocument.uri = params.textDocument.uri.replace(FILE, "")
        log.debug "params: ${params}"
        try {
            result = CompletableFuture.supplyAsync {
                Map<String, List<TextEdit>> edits = languageService.rename(params)
                log.debug("edits: ${edits}")
                textFileService.changeFiles(edits)
                compile(textFileService.changedFiles)
                WorkspaceEdit edit = new WorkspaceEdit(externalUris(edits))
                edit
            }
        } catch (Exception e) {
            log.error(GENERAL_EXCEPTION, e)
            throw new NotImplementedException()
        }
        BigDecimal elapsed = (System.currentTimeMillis() - start) / MILLIS_PER_SECOND
        log.info("Completed in $elapsed ms")
        return result
    }

    static Map<String, List<TextEdit>> externalUris(Map<String, List<TextEdit>> edits) {
        Map<String, List<TextEdit>> result = [:]
        edits.entrySet().each {
            result.put(FILE + it, edits.get(it))
        }
        return result
    }

    @Override
    void didOpen(DidOpenTextDocumentParams params) {
        params.textDocument.uri = params.textDocument.uri.replace(FILE, "")
        textFileService.didOpen(params)
    }

    @Override
    void didChange(DidChangeTextDocumentParams params) {
        params.textDocument.uri = params.textDocument.uri.replace(FILE, "")
        textFileService.didChange(params)
    }

    @Override
    void didClose(DidCloseTextDocumentParams params) {
        params.textDocument.uri = params.textDocument.uri.replace(FILE, "")
        textFileService.didClose(params)
    }

    @Override
    void didSave(DidSaveTextDocumentParams params) {
        try {
            params.textDocument.uri = params.textDocument.uri.replace(FILE, "")
            textFileService.didSave(params)
            compile(textFileService.changedFiles)
        } catch (Exception e) {
            log.error(GENERAL_EXCEPTION, e)
        }
    }

    void compile(Map<String, String> changedFiles = [:]) {
        try {
            LanguageService languageService = new LanguageService()
            GroovyCompilerService compilerService = new GroovyCompilerService(rootUri, languageService, indexerConfig)
            Map<String, List<Diagnostic>> diagnostics = compilerService.compile(changedFiles)
            indexerConfig.scanDependencies = false
            this.languageService = languageService

            if (indexerConfig.serializeLanguageService) {
                serializationService.serialize(ConfigService.getConfigDir(rootUri), languageService)
            }

            sendDiagnostics(diagnostics, client)
        } catch (Exception e) {
            log.error(GENERAL_EXCEPTION, e)
        }
    }

    void setRootUri(URI uri) {
        this.rootUri = uri
    }

}
