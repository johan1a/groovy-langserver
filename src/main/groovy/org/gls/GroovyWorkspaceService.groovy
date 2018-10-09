package org.gls

import java.util.concurrent.CompletableFuture
import org.eclipse.lsp4j.DidChangeWatchedFilesParams
import org.eclipse.lsp4j.DidChangeConfigurationParams
import org.eclipse.lsp4j.WorkspaceSymbolParams
import org.eclipse.lsp4j.services.WorkspaceService
import org.eclipse.lsp4j.SymbolInformation
import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j

@Slf4j
@TypeChecked
class GroovyWorkspaceService implements WorkspaceService {

    @Override
        CompletableFuture<List<? extends SymbolInformation>> symbol(WorkspaceSymbolParams params) {
        log.info "symbol"
        return CompletableFuture.completedFuture([])
    }

    @Override
    void didChangeConfiguration(DidChangeConfigurationParams params) {
        log.info "didChangeConfiguration"
    }

    @Override
    void didChangeWatchedFiles(DidChangeWatchedFilesParams params) {
        log.info "didChangeWatchedFiles"
    }

}
