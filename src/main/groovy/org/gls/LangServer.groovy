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
import org.eclipse.lsp4j.services.LanguageServer
import org.eclipse.lsp4j.services.TextDocumentService
import org.eclipse.lsp4j.services.WorkspaceService
import org.gls.lang.ReferenceStorage
import org.codehaus.groovy.control.CompilationUnit
import org.codehaus.groovy.control.ErrorCollector
import org.codehaus.groovy.syntax.SyntaxException
import org.eclipse.lsp4j.services.LanguageClient
import org.eclipse.lsp4j.*
import org.gls.groovy.GroovyIndexer


@Slf4j
@TypeChecked
class LangServer implements LanguageServer {

    public static final String DEFAULT_SRC_DIR = "/src/main/groovy"
    private WorkspaceService workspaceService
    private GroovyTextDocumentService textDocumentService

    GroovyIndexer getIndexer() {
        return textDocumentService.indexer
    }

    LangServer() {
        this.workspaceService = new GroovyWorkspaceService()
        this.textDocumentService = new GroovyTextDocumentService()
    }

    @Override
    CompletableFuture<InitializeResult> initialize(InitializeParams initializeParams) {
        log.info "initialize: ${initializeParams}"
        textDocumentService.showClientMessage("Initializing langserver capabilities...")

        URI rootUri = new URI(initializeParams.getRootUri() + DEFAULT_SRC_DIR)
        log.info "rootUri: " + rootUri
        ReferenceFinder finder = new ReferenceFinder()
        GroovyIndexer indexer = new GroovyIndexer(rootUri, finder)

        textDocumentService.setReferenceStorage(finder)
        textDocumentService.setIndexer(indexer)

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
