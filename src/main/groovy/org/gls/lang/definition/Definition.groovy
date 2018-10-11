package org.gls.lang.definition

import org.gls.lang.HasLocation
import org.gls.lang.ReferenceStorage

/**
 * Created by johan on 4/9/18.
 */
interface Definition<D, R> extends HasLocation {

    Set<R> findMatchingReferences(ReferenceStorage storage, Set < D > definitions, Set<R> references)

    void setReferences(Set<R> references)

    Set<R> getReferences()

    void setName(String name)
}
