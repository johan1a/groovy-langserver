package org.gls.lang

import groovy.transform.TypeChecked
import org.eclipse.lsp4j.Location
import org.eclipse.lsp4j.Range
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@TypeChecked
class ImmutableLocation extends Location implements Comparable<ImmutableLocation> {

    ImmutableLocation(@NonNull final String uri, @NonNull final ImmutableRange range) {
        super(uri, range)
    }

    @Override
    void setUri(@NonNull final String uri) {
    }

    @Override
    void setRange(@NonNull Range range) {
    }

    @Override
    int compareTo(ImmutableLocation b) {
        if (range.start.line == b.range.start.line) {
            range.start.character <=> b.range.start.character
        } else {
            range.start.line <=> b.range.start.line
        }
    }
}
