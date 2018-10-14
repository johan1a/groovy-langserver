package org.gls.lang.reference

import groovy.transform.ToString
import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotatedNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.DynamicVariable
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.Variable
import org.codehaus.groovy.ast.expr.ClassExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.gls.exception.NotImplementedException
import org.gls.lang.ImmutableLocation
import org.gls.lang.LocationFinder
import org.gls.lang.ReferenceStorage
import org.gls.lang.definition.VarDefinition

@Slf4j
@TypeChecked
@ToString
@SuppressWarnings(["UnusedMethodParameter", "Instanceof", "CatchException"])
// TODO Remove
class VarReference implements Reference<VarDefinition> {

    ImmutableLocation location

    String varName
    String typeName
    // TODO use position
    int definitionLineNumber
    int definitionStartColumn
    Optional<String> declaringClass = Optional.empty()
    private VarDefinition definition

    private static final String NO_VAR_DECL = "no var decl"

    VarReference(String sourceFileURI, List<String> source, ClassNode currentClass, Variable variable) {
        throw new NotImplementedException(variable.toString())
    }

    VarReference(String sourceFileURI, List<String> source, ClassNode currentClass, ASTNode node) {
        if (node instanceof ClassExpression) {
            initDeclarationReference(currentClass, sourceFileURI, source, node as ClassExpression)
        } else if (node instanceof VariableExpression) {
            initDeclarationReference(currentClass, sourceFileURI, source, node as VariableExpression)
        }
        if (varName != null) {
            this.location = LocationFinder.findLocation(sourceFileURI, source, node, varName)
        }
    }

    VarReference(String sourceFileURI, List<String> source, ClassNode currentClass, Parameter parameter) {
        initDeclarationReference(currentClass, sourceFileURI, source, parameter)
        if (varName != null) {
            this.location = LocationFinder.findLocation(sourceFileURI, source, parameter, varName)
        }
    }

    VarReference(String sourceFileURI, List<String> source, ClassNode currentClass, FieldNode node) {
        initDeclarationReference(currentClass, sourceFileURI, source, node)
        if (varName != null) {
            this.location = LocationFinder.findLocation(sourceFileURI, source, node, varName)
            definitionLineNumber = location.range.start.line
            definitionStartColumn = location.range.start.character
        }
    }

    VarReference(String sourceFileURI, List<String> source, ClassNode currentClass, VariableExpression expression) {
        initDeclarationReference(currentClass, sourceFileURI, source, expression)
        if (varName != null) {
            this.location = LocationFinder.findLocation(sourceFileURI, source, expression, varName)
        }
    }

    void initDeclarationReference(ClassNode currentClass, String sourceFileURI, List<String> source, Parameter
            expression) {
        try {
            // TODO Maybe shouldn't be a varusage?
            varName = expression.name
            typeName = expression.type.name
            declaringClass = Optional.of(currentClass.text)

            ImmutableLocation definitionLocation = LocationFinder.findLocation(sourceFileURI, source, expression,
                    varName)
            this.definitionLineNumber = definitionLocation.range.start.line
            definitionStartColumn = definitionLocation.range.start.character
        } catch (Exception e) {
            log.error(NO_VAR_DECL, e)
        }
    }

    void initDeclarationReference(ClassNode currentClass, String sourceFileURI, List<String> source, FieldNode
            expression) {
        try {
            // TODO Maybe shouldn't be a varusage?
            varName = expression.name
            typeName = expression.type.name
            declaringClass = Optional.of(currentClass.text)
            definitionLineNumber = expression.type.lineNumber - 1
            definitionStartColumn = expression.type.columnNumber - 1
        } catch (Exception e) {
            log.error(NO_VAR_DECL, e)
        }
    }

    void initDeclarationReference(ClassNode currentClass, String sourceFileURI, List<String> source, Variable
            expression) {
        try {
            // TODO Maybe shouldn't be a varusage?
            varName = expression.name
            typeName = expression.type.name
            declaringClass = Optional.of(currentClass.text)
            definitionLineNumber = expression.type.lineNumber - 1
            definitionStartColumn = expression.type.columnNumber - 1
        } catch (Exception e) {
            log.error(NO_VAR_DECL, e)
        }
    }

    void initDeclarationReference(ClassNode currentClass, String sourceFileURI, List<String> source, ClassExpression
            expression) {
        try {
            // TODO Maybe shouldn't be a varusage?
            varName = expression.type.name
            typeName = expression.type.name
            declaringClass = Optional.of(typeName)
            definitionLineNumber = expression.type.lineNumber - 1
            definitionStartColumn = expression.type.columnNumber - 1
        } catch (Exception e) {
            log.error(NO_VAR_DECL, e)
        }
    }

    @SuppressWarnings(["CouldBeSwitchStatement"])
    void initDeclarationReference(ClassNode currentClass, String sourceFileURI, List<String> source,
                                  VariableExpression expression) {
        try {
            typeName = expression.type.name
            varName = expression.name
            if (expression.accessedVariable != null) {
                Variable accessed = expression.accessedVariable
                if (accessed instanceof FieldNode) {
                    initFieldNode(currentClass, sourceFileURI, source, accessed as FieldNode)
                } else if (accessed instanceof AnnotatedNode) {
                    initAnnotatedNode(currentClass, sourceFileURI, source, accessed as AnnotatedNode)
                } else if (accessed instanceof DynamicVariable) {
                    initDynamicVariable(currentClass, sourceFileURI, source, accessed as DynamicVariable)
                } else {
                    log.error " cast: ${expression}"
                    log.error " accessed: ${accessed}"
                }
            } else if (expression.isThisExpression()) {
                this.definitionLineNumber = expression.type.lineNumber - 1
                definitionStartColumn = expression.type.columnNumber - 1
            } else if (expression.isSuperExpression()) {
                ClassNode superClass = currentClass.superClass
                this.definitionLineNumber = superClass.lineNumber - superClass.annotations.size()
                definitionStartColumn = expression.type.columnNumber - 1
            } else {
                log.debug "No parentLineNumber: ${expression.name}"
                log.debug("expression: ${expression}")
                //TODO what then?
            }
        } catch (Exception e) {
            log.error(NO_VAR_DECL, e)
        }
    }

    void initFieldNode(ClassNode currentClass, String sourceFileURI, List<String> source, FieldNode fieldNode) {
        ImmutableLocation definitionLocation = LocationFinder.findLocation(sourceFileURI, source, fieldNode, varName)
        this.definitionLineNumber = definitionLocation.range.start.line
        definitionStartColumn = definitionLocation.range.start.character
        this.declaringClass = Optional.of(currentClass.name)
    }

    void initDynamicVariable(ClassNode currentClass, String sourceFileURI, List<String> source,
                             DynamicVariable varDeclaration) {
        this.definitionLineNumber = varDeclaration.type.lineNumber - 1
        definitionStartColumn = varDeclaration.type.columnNumber - 1
        this.declaringClass = Optional.of(currentClass.name)
    }

    void initAnnotatedNode(ClassNode currentClass, String sourceFileURI, List<String> source,
                           AnnotatedNode varDeclaration) {
        ImmutableLocation definitionLocation = LocationFinder.findLocation(sourceFileURI, source, varDeclaration,
                varName)
        this.definitionLineNumber = definitionLocation.range.start.line
        definitionStartColumn = definitionLocation.range.start.character
        if (varDeclaration.declaringClass != null) {
            this.declaringClass = Optional.of(varDeclaration.declaringClass.name)
        } else {
            // TODO not sure if this is correct.
            // Seems to be true for method arguments.
            this.declaringClass = Optional.of(currentClass.name)
        }
    }

    @Override
    void setDefinition(VarDefinition definition) {
        this.definition = definition
    }

    @Override
    Optional<VarDefinition> getDefinition() {
        return Optional.ofNullable(definition)
    }

    @Override
    Optional<VarDefinition> findMatchingDefinition(ReferenceStorage storage, Set<VarDefinition> definitions) {
        return Optional.ofNullable(definitions.find {
            it.sourceFileURI == sourceFileURI &&
                    it.typeName == typeName &&
                    it.varName == varName &&
                    it.lineNumber == definitionLineNumber
        })
    }

}
