package org.gls.lang

import groovy.transform.TypeChecked
import org.eclipse.lsp4j.Location
import org.eclipse.lsp4j.Range
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

/**
 * Created by johan on 4/8/18.
 */
@TypeChecked
class ImmutableLocation extends Location {

    public ImmutableLocation(@NonNull final String uri, @NonNull final ImmutableRange range) {
        super(uri, range)
    }

    @Override
    public void setUri(@NonNull final String uri) {
    }

    @Override
    void setRange(@NonNull Range range) {
    }
}
