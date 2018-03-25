package org.gls.lang

trait Definition {

    abstract String getSourceFileURI()

    String getURI() {
        return "file://$sourceFileURI"
    }
}
