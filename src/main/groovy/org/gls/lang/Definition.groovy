package org.gls.lang

/**
 * Created by johan on 4/9/18.
 */
interface Definition<R> extends HasLocation {

    Set<R> findMatchingReferences(Set<R> references)

    void setReferences(Set<R> references)

}