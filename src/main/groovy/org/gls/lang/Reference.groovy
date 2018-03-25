package org.gls.lang

interface Reference {

    String getSourceFileURI()
    int getColumnNumber()
    int getLastColumnNumber()
    int getLineNumber()
    int getLastLineNumber()

}
