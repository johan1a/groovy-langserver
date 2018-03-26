package org.gls


import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
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

@Slf4j
@TypeChecked
class LangServer implements LanguageServer, LanguageClientAware {

    private LanguageClient client
    private WorkspaceService workspaceService
    private GroovyTextDocumentService textDocumentService
    private GroovyIndexer indexer

    public LangServer() {
        this.workspaceService = new GroovyWorkspaceService()
        this.textDocumentService = new GroovyTextDocumentService()
    }

    @Override
    CompletableFuture<InitializeResult> initialize(InitializeParams initializeParams) {
        log.info "initialize: ${initializeParams}"
        client.showMessage(new MessageParams(MessageType.Info, "Initializing langserver capabilities..."))

        URI rootUri = new URI(initializeParams.getRootUri())
        log.info "rootUri: " + rootUri
        ReferenceStorage storage = new ReferenceStorage()
        indexer = new GroovyIndexer(rootUri, storage)
        indexer.indexRecursive()
        textDocumentService.setReferenceStorage(storage)

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
