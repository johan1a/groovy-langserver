package org.gls.lang.types

import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.util.logging.Slf4j
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.gls.lang.ReferenceStorage
import org.gls.lang.definition.FuncDefinition

@Slf4j
// TODO remove supress
@SuppressWarnings("UnusedMethodParameter")
class SimpleExpression {

    String name

    String containingClass

    @JsonIgnore
    Expression expression

    Type type

    Type resolve(ReferenceStorage storage) {
        resolve(storage, expression)
    }

    Type resolve(ReferenceStorage storage, Expression expression) {
        log.info "aa"
    }

    Type resolve(ReferenceStorage storage, ConstantExpression expression) {
        new SimpleClass(name: expression.type.name)
    }

    ArgumentType resolve(ReferenceStorage storage, ArgumentListExpression expression) {
        new ArgumentType(types: expression.expressions.collect {
            resolve(storage, it)
        })
    }

    // TODO remove supress
    @SuppressWarnings("UnusedMethodParameter")
    Type resolve(ReferenceStorage storage, VariableExpression expression) {
        ClassNode type = expression.accessedVariable?.type
        if (!type) {
            return null
        }
        new SimpleClass(name: type.name, type: type)
    }

    Type resolve(ReferenceStorage storage, MethodCallExpression expression) {
        Expression objectExpression = expression.objectExpression
        if (objectExpression.text == "this") {
            String methodName = expression.methodAsString
            ArgumentType arguments = resolve(storage, expression.arguments) as ArgumentType
            if (!arguments) {
                return null
            }
            FuncDefinition funcDefinition = storage.funcDefinitions.find { FuncDefinition func ->
                Type type = func.parameterTypes.resolve(storage)
                if (!type) {
                    return null
                }
                List<Type> definitionParameterTypes = type.types
                List<Type> functionCallParameterTypes = arguments.types
                func.functionName == methodName &&
                        func.definingClass == containingClass &&
                        definitionParameterTypes == functionCallParameterTypes
            }
            // TODO return type should be simpleclass
            new SimpleClass(name: funcDefinition.returnType)
        } else {
            null
        }
    }
}
