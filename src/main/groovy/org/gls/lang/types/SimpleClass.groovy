package org.gls.lang.types

import org.codehaus.groovy.ast.ClassNode

class SimpleClass implements Type {
    String name
    ClassNode type

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        SimpleClass that = (SimpleClass) o

        if (name != that.name) return false

        return true
    }

    int hashCode() {
        return (name != null ? name.hashCode() : 0)
    }
}
