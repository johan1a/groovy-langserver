package org.gls.lang

trait HasLocation {

    abstract ImmutableLocation getLocation()

    int getLineNumber() { return location.range.start.line }

    int getLastLineNumber() { return location.range.end.line }

    int getColumnNumber() { return location.range.start.character }

    int getLastColumnNumber() { return location.range.end.character }

    String getSourceFileURI() { return location.uri }
}
