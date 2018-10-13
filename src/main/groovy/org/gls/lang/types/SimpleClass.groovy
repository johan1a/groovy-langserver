package org.gls.lang.types

import org.codehaus.groovy.ast.ClassNode

class SimpleClass implements Type {
    String name
    ClassNode type
    List<Type> genericTypes = []

    @Override
    String toString(){
        String result = name
        if(!genericTypes.isEmpty()){
            result += "<"
            result += genericTypes.collect{
                it.toString() + ","
            }
            result += ">"
        }
        result
    }

    boolean equals(Object o) {
        if (this.is(o)) {
            return true
        }
        if (getClass() != o.class) {
            return false
        }

        SimpleClass that = (SimpleClass) o
        if (genericTypes != that.genericTypes) return false

        return name == that.name
    }

    int hashCode() {
        return (name != null ? name.hashCode() : 0)
    }
}
