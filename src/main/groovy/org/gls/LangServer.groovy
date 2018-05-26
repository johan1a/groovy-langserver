package org.gls


import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j

import java.util.concurrent.CompletableFuture

import org.eclipse.lsp4j.InitializeParams
import org.eclipse.lsp4j.InitializeResult
import org.eclipse.lsp4j.ServerCapabilities
import org.eclipse.lsp4j.jsonrpc.Launcher
import org.eclipse.lsp4j.launch.LSPLauncher
import org.eclipse.lsp4j.services.LanguageServer
import org.eclipse.lsp4j.services.WorkspaceService
import org.eclipse.lsp4j.services.LanguageClient

@Slf4j
@TypeChecked
class LangServer implements LanguageServer {

    public static final String DEFAULT_SRC_DIR = "/src/main/groovy"
    private WorkspaceService workspaceService
    GroovyTextDocumentService textDocumentService

    LangServer() {
        this.workspaceService = new GroovyWorkspaceService()
        this.textDocumentService = new GroovyTextDocumentService(new IndexerConfig(scanAllSubDirs: false, scanDependencies: true))
    }

    @Override
    CompletableFuture<InitializeResult> initialize(InitializeParams initializeParams) {
        textDocumentService.showClientMessage("Initializing langserver capabilities...")
        log.info("rootUri: ${new URI(initializeParams.getRootUri())}")

        textDocumentService.setRootUri(new URI(initializeParams.getRootUri()))
        textDocumentService.index()

        ServerCapabilities capabilities = new ServerCapabilities()
        return CompletableFuture.completedFuture(new InitializeResult(capabilities))
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
    GroovyTextDocumentService getTextDocumentService() {
        return textDocumentService
    }

    static void main(String[] args) {
        log.info "Starting langserver"
        LangServer server = new LangServer()
        Launcher<LanguageClient> launcher = LSPLauncher.createServerLauncher(server, System.in,
                    System.out)
        LanguageClient client = launcher.getRemoteProxy()
        server.textDocumentService.connect(client)
        launcher.startListening()
    }
}
