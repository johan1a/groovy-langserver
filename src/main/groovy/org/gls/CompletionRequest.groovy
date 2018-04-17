package org.gls

import org.eclipse.lsp4j.Position

/**
 * Created by johan on 4/17/18.
 */
class CompletionRequest {
    String uri
    Position position
    String precedingText
}
