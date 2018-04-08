package org.gls.lang

import org.eclipse.lsp4j.Position
import org.eclipse.lsp4j.Range
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

/**
 * Created by johan on 4/8/18.
 */
class ImmutableRange extends Range {

    public ImmutableRange(@NonNull final Position start, @NonNull final Position end) {
        super(start, end)
    }

    @Override
    void setEnd(@NonNull Position end) {
    }

    @Override
    void setStart(@NonNull Position start) {
    }
}
