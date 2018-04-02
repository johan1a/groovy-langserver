package org.gls


import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.gls.lang.ReferenceFinder

import java.util.concurrent.CompletableFuture

import org.eclipse.lsp4j.InitializeParams
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.MessageType;
import org.eclipse.lsp4j.ServerCapabilities
import org.eclipse.lsp4j.jsonrpc.Launcher
import org.eclipse.lsp4j.launch.LSPLauncher
import org.eclipse.lsp4j.services.LanguageClient
import org.eclipse.lsp4j.services.LanguageClientAware
import org.eclipse.lsp4j.services.LanguageServer
import org.eclipse.lsp4j.services.TextDocumentService
import org.eclipse.lsp4j.services.WorkspaceService
import org.gls.groovy.GroovyIndexer
import org.gls.lang.ReferenceStorage
import org.codehaus.groovy.control.messages.*
import org.codehaus.groovy.control.CompilationUnit
import org.codehaus.groovy.control.ErrorCollector
import org.codehaus.groovy.syntax.SyntaxException
import org.eclipse.lsp4j.*


@Slf4j
@TypeChecked
class LangServer implements LanguageServer, LanguageClientAware {

    public static final String DEFAULT_SRC_DIR = "/src/main/groovy"
    private LanguageClient client
    private WorkspaceService workspaceService
    private GroovyTextDocumentService textDocumentService

    GroovyIndexer indexer

    GroovyIndexer getIndexer() {
        return indexer
    }

    LangServer() {
        this.workspaceService = new GroovyWorkspaceService()
        this.textDocumentService = new GroovyTextDocumentService()
    }

    @Override
    CompletableFuture<InitializeResult> initialize(InitializeParams initializeParams) {
        log.info "initialize: ${initializeParams}"
        showClientMessage("Initializing langserver capabilities...")

        URI rootUri = new URI(initializeParams.getRootUri() + DEFAULT_SRC_DIR)
        log.info "rootUri: " + rootUri
        ReferenceFinder finder = new ReferenceFinder()
        indexer = new GroovyIndexer(rootUri, finder)
        indexer.indexRecursive()
        textDocumentService.setReferenceStorage(finder)
        sendDiagnostics(indexer.getErrorCollector(), client)

        ServerCapabilities capabilities = new ServerCapabilities()
        return CompletableFuture.completedFuture(new InitializeResult(capabilities))
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


    private void showClientMessage(String message) {
        client?.showMessage(new MessageParams(MessageType.Info, message))
    }

    @Override
    CompletableFuture shutdown(){
        CompletableFuture.completedFuture("")
    }

    @Override
    void exit(){
        log.info "Stopping langserver"
    }

    @Override
    WorkspaceService getWorkspaceService() {
        return workspaceService
    }

    @Override
    TextDocumentService getTextDocumentService() {
        return textDocumentService
    }

    @Override
    void connect(LanguageClient client) {
        log.info "Connected to client."
        this.client = client
    }

    static void main(String[] args) {
        log.info "Starting langserver"
        LanguageServer server = new LangServer()
        Launcher<LanguageClient> launcher = LSPLauncher.createServerLauncher(server, System.in,
                    System.out)
        LanguageClient client = launcher.getRemoteProxy()
        server.connect(client)
        launcher.startListening()
    }
}
