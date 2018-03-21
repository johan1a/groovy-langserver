package org.gls

import groovy.transform.TypeChecked
import java.util.concurrent.CompletableFuture
import org.eclipse.lsp4j.InitializeParams
import org.eclipse.lsp4j.InitializeParams
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.services.LanguageServer
import org.eclipse.lsp4j.services.TextDocumentService
import org.eclipse.lsp4j.services.WorkspaceService
import org.eclipse.lsp4j.services.LanguageClient
import org.eclipse.lsp4j.launch.LSPLauncher
import org.eclipse.lsp4j.jsonrpc.Launcher
import org.eclipse.lsp4j.services.LanguageClientAware
import org.eclipse.lsp4j.services.LanguageClient
import org.eclipse.lsp4j.ServerCapabilities

import groovy.util.logging.Slf4j

@Slf4j
@TypeChecked
class LangServer implements LanguageServer, LanguageClientAware {

    private LanguageClient client
    private WorkspaceService workspaceService
    private TextDocumentService textDocumentService

    public LangServer() {
        this.workspaceService = new GroovyWorkspaceService()
        this.textDocumentService = new GroovyTextDocumentService()
    }

    @Override
    CompletableFuture<InitializeResult> initialize(InitializeParams initializeParams) {
        ServerCapabilities capabilities = new ServerCapabilities()
        return CompletableFuture.completedFuture(new InitializeResult(capabilities))
    }

    @Override
    CompletableFuture shutdown(){
        CompletableFuture.completedFuture("")
    }

    @Override
    void exit(){
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
        this.client = client
    }

    static void main(String[] args) {
        log.info "Starting langserver"
        LanguageServer server = new LangServer()
        Launcher<LanguageClient> launcher = LSPLauncher.createServerLauncher(server, System.in,
                    System.out)
        LanguageClient client = launcher.getRemoteProxy();
        server.connect(client);
        launcher.startListening();
    }

}
