package org.gls.lang.types

import org.codehaus.groovy.ast.Parameter
import org.gls.lang.ReferenceStorage

class ParameterExpression extends SimpleExpression {

    List<Parameter> parameters


    Type resolve(ReferenceStorage storage) {
        //return new SimpleClass(name: parameter.type.name, type: parameter.type)
        new ArgumentType(types: parameters.collect{
                new SimpleClass(name: it.type.name, type: it.type)
        })
    }
}
