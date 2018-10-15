package org.gls.lang.types

import com.fasterxml.jackson.annotation.JsonIgnore
import org.codehaus.groovy.ast.Parameter
import org.gls.lang.ReferenceStorage

class ParameterExpression extends SimpleExpression {

    @JsonIgnore
    List<Parameter> parameters

    @Override
    Type resolve(ReferenceStorage storage) {
        //return new SimpleClass(name: parameter.type.name, type: parameter.type)
        new ArgumentType(types: parameters.collect {
            new SimpleClass(name: it.type.name, type: it.type)
        })
    }
}
