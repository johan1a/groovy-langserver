package org.gls.lang

import org.eclipse.lsp4j.Location
import org.eclipse.lsp4j.Position
import org.eclipse.lsp4j.Range

trait HasLocation {

    abstract int getLineNumber()
    abstract int getLastLineNumber()
    abstract int getColumnNumber()
    abstract int getLastColumnNumber()
    abstract String getSourceFileURI()

    Location getLocation() {
        ImmutablePosition start = new ImmutablePosition(getLineNumber(), getColumnNumber())
        ImmutablePosition end = new ImmutablePosition(getLastLineNumber(), getLastColumnNumber())
        return new ImmutableLocation(getSourceFileURI(), new ImmutableRange(start, end))
    }
}
