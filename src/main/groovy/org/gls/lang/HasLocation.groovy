package org.gls.lang

import org.eclipse.lsp4j.Location
import org.eclipse.lsp4j.Position
import org.eclipse.lsp4j.Range

trait HasLocation {
    Location getLocation() {
        Position start = new Position(getLineNumber(), getColumnNumber())
        Position end = new Position(getLastLineNumber(), getLastColumnNumber())
        return new Location("file://" + getSourceFileURI(), new Range(start, end))
    }

    abstract int getLineNumber()
    abstract int getLastLineNumber()
    abstract int getColumnNumber()
    abstract int getLastColumnNumber()
    abstract String getSourceFileURI()
}
