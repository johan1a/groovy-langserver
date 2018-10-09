package org.gls.lang.definition

import org.gls.lang.HasLocation

/**
 * Created by johan on 4/9/18.
 */
interface Definition<R> extends HasLocation {

    Set<R> findMatchingReferences(Set<R> references)

    void setReferences(Set<R> references)

    Set<R> getReferences()

    void setName(String name)
}
