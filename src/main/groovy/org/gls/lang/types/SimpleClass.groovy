package org.gls.lang.types

import org.codehaus.groovy.ast.ClassNode

class SimpleClass implements Type {
    String name
    ClassNode type

    boolean equals(Object o) {
        if (this.is(o)) {
            return true
        }
        if (getClass() != o.class) {
            return false
        }

        SimpleClass that = (SimpleClass) o

        return name == that.name
    }

    int hashCode() {
        return (name != null ? name.hashCode() : 0)
    }
}
