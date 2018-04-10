package org.gls.lang.reference

import org.gls.lang.HasLocation

interface Reference<D extends HasLocation> extends HasLocation {

    Optional<D> findMatchingDefinition(Set<D> definitions)

    void setDefinition(D definition)

    Optional<D> getDefinition()

}
