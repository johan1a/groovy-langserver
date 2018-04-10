package org.gls.lang

interface Reference<D extends HasLocation> extends HasLocation {

    Optional<D> findMatchingDefinition(Set<D> definitions)

    void setDefinition(D definition)
}
