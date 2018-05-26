package org.gls.lang

trait HasLocation {

    abstract ImmutableLocation getLocation()

    int getLineNumber() { return location.getRange().start.line }

    int getLastLineNumber() { return location.getRange().end.line }

    int getColumnNumber() { return location.getRange().start.character }

    int getLastColumnNumber() { return location.getRange().end.character }

    String getSourceFileURI() { return location.uri }
}
