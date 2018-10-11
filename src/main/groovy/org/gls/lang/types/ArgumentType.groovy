package org.gls.lang.types

import org.codehaus.groovy.ast.Parameter


class ArgumentType implements Type {
    List<Type> types
    Parameter[] parameters

}
