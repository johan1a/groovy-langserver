package org.gls.lang.types

import com.fasterxml.jackson.annotation.JsonIgnore
import org.codehaus.groovy.ast.Parameter

class ArgumentType implements Type {
    List<Type> types

    @JsonIgnore
    Parameter[] parameters

}
