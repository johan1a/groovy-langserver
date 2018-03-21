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
import groovy.util.logging.*

@Log
@TypeChecked
class LangServer implements LanguageServer {

  @Override
  CompletableFuture<InitializeResult> initialize(InitializeParams initializeParams) {
    return CompletableFuture.completedFuture(new InitializeResult());
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
    return new GroovyWorkspaceService()
  }

  @Override
  TextDocumentService getTextDocumentService() {
    return new GroovyTextDocumentService()
  }

  static void main(String[] args) {
    log.info "Starting langserver"
    LanguageServer server = new LangServer()
    Launcher<LanguageClient> launcher = LSPLauncher.createServerLauncher(server, System.in,
                                       System.out)
    launcher.startListening();
    log.info "Exiting langserver"
  }

}
