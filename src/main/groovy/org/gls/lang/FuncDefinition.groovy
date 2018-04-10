package org.gls.lang

import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression

class FuncDefinition implements Definition<FuncReference> {

    ImmutableLocation location

    String returnType
    String functionName
    String definingClass
    List<String> parameterTypes

    FuncDefinition() {

    }

    FuncDefinition(String sourceFileURI, List<String> source, String definingClass, MethodNode node) {
        functionName = node.getName()
        returnType = node.getReturnType().getName()
        this.definingClass = definingClass
        initParameterTypes(node.getParameters())
        this.location = LocationFinder.findLocation(sourceFileURI, source, node, functionName)
    }

    FuncDefinition(String sourceFileURI, String source, String definingClass, StaticMethodCallExpression expression) {
        functionName = expression.getMethod()
        returnType = expression.type.getName()
        this.definingClass = definingClass
        initParameterTypes(expression.getArguments())
        this.location = LocationFinder.findLocation(sourceFileURI, source, expression, functionName)
    }

    static FuncDefinition makeGetter(String sourceFileURI, List<String> source, String definingClass, FieldNode node) {
        FuncDefinition definition = new FuncDefinition()
        definition.functionName = "get" + node.name.substring(0,1).toUpperCase() + node.name.substring(1)
        definition.returnType = node.type.getName()
        definition.definingClass = definingClass
        definition.parameterTypes = []
        definition.location = LocationFinder.findLocation(sourceFileURI, source, node, node.name)
        return definition
    }

    static FuncDefinition makeSetter(String sourceFileURI, List<String> source, String definingClass, FieldNode node) {
        FuncDefinition definition = new FuncDefinition()
        definition.functionName = "set" + node.name.substring(0,1).toUpperCase() + node.name.substring(1)
        definition.returnType = "void"
        definition.definingClass = definingClass
        definition.parameterTypes = [node.type.getName()]
        definition.location = LocationFinder.findLocation(sourceFileURI, source, node, node.name)
        return definition
    }

    void initParameterTypes(Expression arguments) {
        throw new Exception()
    }
    void initParameterTypes(ArgumentListExpression arguments) {
        this.parameterTypes = arguments.collect { it.getType().name }
    }

    void initParameterTypes(Parameter[] parameters) {
        this.parameterTypes = parameters.collect { it.getType().name }
    }

    @Override
    Set<FuncReference> findMatchingReferences(Set<FuncReference> funcCalls) {
        funcCalls.findAll {
            it.definingClass == getDefiningClass() &&
                    it.functionName == getFunctionName() &&
                    it.argumentTypes == this.parameterTypes
        }
    }

    @Override
    public String toString() {
        return "FuncDefinition{" +
                "sourceFileURI='" + sourceFileURI + '\'' +
                ", columnNumber=" + columnNumber +
                ", lastColumnNumber=" + lastColumnNumber +
                ", lineNumber=" + lineNumber +
                ", lastLineNumber=" + lastLineNumber +
                ", returnType='" + returnType + '\'' +
                ", functionName='" + functionName + '\'' +
                ", definingClass='" + definingClass + '\'' +
                ", parameterTypes=" + parameterTypes +
                '}';
    }
}
