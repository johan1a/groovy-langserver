package org.gls

import java.util.concurrent.CompletableFuture
import org.eclipse.lsp4j.DidChangeWatchedFilesParams
import org.eclipse.lsp4j.DidChangeConfigurationParams
import org.eclipse.lsp4j.WorkspaceSymbolParams
import org.eclipse.lsp4j.services.WorkspaceService
import org.eclipse.lsp4j.SymbolInformation

class GroovyWorkspaceService implements WorkspaceService {


  @Override
  CompletableFuture<List<? extends SymbolInformation>> symbol(WorkspaceSymbolParams params) {
  }

  @Override
  void didChangeConfiguration(DidChangeConfigurationParams params) {

  }

  @Override
  void didChangeWatchedFiles(DidChangeWatchedFilesParams params) {

  }

}
