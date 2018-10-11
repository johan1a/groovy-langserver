package org.gls.lang.reference

import org.gls.lang.HasLocation
import org.gls.lang.ReferenceStorage

interface Reference<D extends HasLocation> extends HasLocation {

    Optional<D> findMatchingDefinition(ReferenceStorage storage, Set < D > definitions)

    void setDefinition(D definition)

    Optional<D> getDefinition()

}
