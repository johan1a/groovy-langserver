package org.gls.groovy

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.codehaus.groovy.ast.ClassCodeVisitorSupport
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.ConstructorNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.PropertyNode
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.ArrayExpression
import org.codehaus.groovy.ast.expr.AttributeExpression
import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.BitwiseNegationExpression
import org.codehaus.groovy.ast.expr.BooleanExpression
import org.codehaus.groovy.ast.expr.CastExpression
import org.codehaus.groovy.ast.expr.ClassExpression
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.ClosureListExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codehaus.groovy.ast.expr.DeclarationExpression
import org.codehaus.groovy.ast.expr.ElvisOperatorExpression
import org.codehaus.groovy.ast.expr.FieldExpression
import org.codehaus.groovy.ast.expr.GStringExpression
import org.codehaus.groovy.ast.expr.ListExpression
import org.codehaus.groovy.ast.expr.MapEntryExpression
import org.codehaus.groovy.ast.expr.MapExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.MethodPointerExpression
import org.codehaus.groovy.ast.expr.NotExpression
import org.codehaus.groovy.ast.expr.PostfixExpression
import org.codehaus.groovy.ast.expr.PrefixExpression
import org.codehaus.groovy.ast.expr.PropertyExpression
import org.codehaus.groovy.ast.expr.RangeExpression
import org.codehaus.groovy.ast.expr.SpreadExpression
import org.codehaus.groovy.ast.expr.SpreadMapExpression
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression
import org.codehaus.groovy.ast.expr.TernaryExpression
import org.codehaus.groovy.ast.expr.TupleExpression
import org.codehaus.groovy.ast.expr.UnaryMinusExpression
import org.codehaus.groovy.ast.expr.UnaryPlusExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.AssertStatement
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.BreakStatement
import org.codehaus.groovy.ast.stmt.CaseStatement
import org.codehaus.groovy.ast.stmt.CatchStatement
import org.codehaus.groovy.ast.stmt.ContinueStatement
import org.codehaus.groovy.ast.stmt.DoWhileStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.stmt.ForStatement
import org.codehaus.groovy.ast.stmt.IfStatement
import org.codehaus.groovy.ast.stmt.ReturnStatement
import org.codehaus.groovy.ast.stmt.SwitchStatement
import org.codehaus.groovy.ast.stmt.SynchronizedStatement
import org.codehaus.groovy.ast.stmt.ThrowStatement
import org.codehaus.groovy.ast.stmt.TryCatchStatement
import org.codehaus.groovy.ast.stmt.WhileStatement
import org.codehaus.groovy.classgen.BytecodeExpression
import org.codehaus.groovy.control.SourceUnit
import org.gls.exception.NotImplementedException
import org.gls.lang.LanguageService
import org.gls.lang.definition.ClassDefinition
import org.gls.lang.definition.FuncDefinition
import org.gls.lang.definition.VarDefinition
import org.gls.lang.reference.ClassReference
import org.gls.lang.reference.FuncReference
import org.gls.lang.reference.VarReference

@Slf4j
@TypeChecked
@SuppressWarnings(["MethodCount", "UnnecessaryOverridingMethod"])
class CodeVisitor extends ClassCodeVisitorSupport {

    private final LanguageService finder
    private final String sourceFileURI
    private ClassNode currentClassNode
    List<String> fileContents

    CodeVisitor(LanguageService finder, String sourceFileURI, List<String> fileContents) {
        this.finder = finder
        this.sourceFileURI = sourceFileURI
        this.fileContents = fileContents
    }

    @Override
    SourceUnit getSourceUnit() {
        throw new NotImplementedException("Not implemented")
    }

    @Override
    void visitClass(ClassNode node) {
        log.debug("Visiting class ${node.name}")
        currentClassNode = node
        finder.addClassDefinition(new ClassDefinition(node, sourceFileURI, fileContents))
        super.visitClass(node)
    }

    @Override
    void visitConstructor(ConstructorNode node) {
        super.visitConstructor(node)
        if (!node.hasNoRealSourcePosition()) {
            finder.addClassUsage(new ClassReference(sourceFileURI, fileContents, node))
            node.parameters.each { Parameter it ->
                finder.addVarDefinition(new VarDefinition(sourceFileURI, fileContents, it))
                finder.addClassUsage(new ClassReference(sourceFileURI, fileContents, it))
            }
        }
    }

    @Override
    void visitField(FieldNode node) {
        finder.addClassUsage(new ClassReference(sourceFileURI, fileContents, node))
        finder.addVarDefinition(new VarDefinition(sourceFileURI, fileContents, node))
        VarReference varReference = new VarReference(sourceFileURI, fileContents, currentClassNode, node)
        log.debug("field varreference: $varReference")
        finder.addVarUsage(varReference)
        super.visitField(node)
    }

    @Override
    void visitMethod(MethodNode node) {
        finder.addClassUsage(new ClassReference(sourceFileURI, fileContents, node))
        finder.addFuncDefinition(new FuncDefinition(sourceFileURI, fileContents, currentClassNode.name, node))
        node.parameters.each { Parameter it ->
            finder.addVarDefinition(new VarDefinition(sourceFileURI, fileContents, it))
            finder.addClassUsage(new ClassReference(sourceFileURI, fileContents, it))
        }
        super.visitMethod(node)
    }

    @Override
    void visitProperty(PropertyNode node) {
        FuncDefinition getter = FuncDefinition.makeGetter(sourceFileURI, fileContents, currentClassNode.name,
                node.field)
        finder.addFuncDefinition(getter)
        FuncDefinition setter = FuncDefinition.makeSetter(sourceFileURI, fileContents, currentClassNode.name,
                node.field)
        finder.addFuncDefinition(setter)
        super.visitProperty(node)
    }

    @Override
    void visitArgumentlistExpression(ArgumentListExpression expression) {
        super.visitArgumentlistExpression(expression)
    }

    @Override
    void visitArrayExpression(ArrayExpression expression) {
        super.visitArrayExpression(expression)
    }

    @Override
    void visitAssertStatement(AssertStatement statement) {
        super.visitAssertStatement(statement)
    }

    @Override
    void visitAttributeExpression(AttributeExpression attributeExpression) {
        super.visitAttributeExpression(attributeExpression)
    }

    @Override
    void visitBinaryExpression(BinaryExpression expression) {
        super.visitBinaryExpression(expression)
    }

    @Override
    void visitBitwiseNegationExpression(BitwiseNegationExpression expression) {
        super.visitBitwiseNegationExpression(expression)
    }

    @Override
    void visitBlockStatement(BlockStatement statement) {
        super.visitBlockStatement(statement)
    }

    @Override
    void visitBooleanExpression(BooleanExpression expression) {
        super.visitBooleanExpression(expression)
    }

    @Override
    void visitBreakStatement(BreakStatement statement) {
        super.visitBreakStatement(statement)
    }

    @Override
    void visitBytecodeExpression(BytecodeExpression expression) {
        super.visitBytecodeExpression(expression)
    }

    @Override
    void visitCaseStatement(CaseStatement statement) {
        super.visitCaseStatement(statement)
    }

    @Override
    void visitCastExpression(CastExpression expression) {
        super.visitCastExpression(expression)
    }

    @Override
    void visitCatchStatement(CatchStatement statement) {
        super.visitCatchStatement(statement)
    }

    @Override
    void visitClassExpression(ClassExpression expression) {
        super.visitClassExpression(expression)
    }

    @Override
    void visitClosureExpression(ClosureExpression expression) {
        super.visitClosureExpression(expression)
    }

    @Override
    void visitClosureListExpression(ClosureListExpression closureListExpression) {
        super.visitClosureListExpression(closureListExpression)
    }

    @Override
    void visitConstantExpression(ConstantExpression expression) {
        super.visitConstantExpression(expression)
    }

    @Override
    void visitConstructorCallExpression(ConstructorCallExpression expression) {
        super.visitConstructorCallExpression(expression)
    }

    @Override
    void visitContinueStatement(ContinueStatement statement) {
        super.visitContinueStatement(statement)
    }

    @Override
    @SuppressWarnings("EmptyIfStatement")
    void visitDeclarationExpression(DeclarationExpression expression) {
        if (expression.isMultipleAssignmentDeclaration()) {
            //TODO TupleExpression left = expression.getTupleExpression()
        } else {
            VariableExpression left = expression.variableExpression
            finder.addVarDefinition(new VarDefinition(sourceFileURI, fileContents, left))
            finder.addClassUsage(new ClassReference(sourceFileURI, fileContents, expression))
        }
        super.visitDeclarationExpression(expression)
    }

    @Override
    void visitDoWhileLoop(DoWhileStatement loop) {
        super.visitDoWhileLoop(loop)
    }

    @Override
    void visitExpressionStatement(ExpressionStatement statement) {
        super.visitExpressionStatement(statement)
    }

    @Override
    void visitFieldExpression(FieldExpression expression) {
        super.visitFieldExpression(expression)
    }

    @Override
    void visitForLoop(ForStatement forLoop) {
        super.visitForLoop(forLoop)
    }

    @Override
    void visitGStringExpression(GStringExpression expression) {
        super.visitGStringExpression(expression)
    }

    @Override
    void visitIfElse(IfStatement ifElse) {
        super.visitIfElse(ifElse)
    }

    @Override
    void visitListExpression(ListExpression expression) {
        super.visitListExpression(expression)
    }

    @Override
    void visitMapEntryExpression(MapEntryExpression expression) {
        super.visitMapEntryExpression(expression)
    }

    @Override
    void visitMapExpression(MapExpression expression) {
        super.visitMapExpression(expression)
    }

    @Override
    void visitMethodCallExpression(MethodCallExpression call) {
        VarReference usage = new VarReference(sourceFileURI, fileContents, currentClassNode, call.receiver)

        FuncReference funcCall = new FuncReference(sourceFileURI, fileContents, currentClassNode, call, usage)
        finder.addFuncCall(funcCall)
        super.visitMethodCallExpression(call)
    }

    @Override
    void visitMethodPointerExpression(MethodPointerExpression expression) {
        super.visitMethodPointerExpression(expression)
    }

    @Override
    void visitNotExpression(NotExpression expression) {
        super.visitNotExpression(expression)
    }

    @Override
    void visitPostfixExpression(PostfixExpression expression) {
        super.visitPostfixExpression(expression)
    }

    @Override
    void visitPrefixExpression(PrefixExpression expression) {
        super.visitPrefixExpression(expression)
    }

    @Override
    void visitPropertyExpression(PropertyExpression expression) {
        super.visitPropertyExpression(expression)
    }

    @Override
    void visitRangeExpression(RangeExpression expression) {
        super.visitRangeExpression(expression)
    }

    @Override
    void visitReturnStatement(ReturnStatement statement) {
        super.visitReturnStatement(statement)
    }

    @Override
    void visitShortTernaryExpression(ElvisOperatorExpression expression) {
        super.visitShortTernaryExpression(expression)
    }

    @Override
    void visitSpreadExpression(SpreadExpression expression) {
        super.visitSpreadExpression(expression)
    }

    @Override
    void visitSpreadMapExpression(SpreadMapExpression expression) {
        super.visitSpreadMapExpression(expression)
    }

    @Override
    void visitStaticMethodCallExpression(StaticMethodCallExpression expression) {
        finder.addFuncCall(new FuncReference(sourceFileURI, fileContents, currentClassNode, expression))
        super.visitStaticMethodCallExpression(expression)
    }

    @Override
    void visitSwitch(SwitchStatement statement) {
        super.visitSwitch(statement)
    }

    @Override
    void visitSynchronizedStatement(SynchronizedStatement statement) {
        super.visitSynchronizedStatement(statement)
    }

    @Override
    void visitTernaryExpression(TernaryExpression expression) {
        super.visitTernaryExpression(expression)
    }

    @Override
    void visitThrowStatement(ThrowStatement statement) {
        super.visitThrowStatement(statement)
    }

    @Override
    void visitTryCatchFinally(TryCatchStatement finally1) {
        super.visitTryCatchFinally(finally1)
    }

    @Override
    void visitTupleExpression(TupleExpression expression) {
        super.visitTupleExpression(expression)
    }

    @Override
    void visitUnaryMinusExpression(UnaryMinusExpression expression) {
        super.visitUnaryMinusExpression(expression)
    }

    @Override
    void visitUnaryPlusExpression(UnaryPlusExpression expression) {
        super.visitUnaryPlusExpression(expression)
    }

    @Override
    void visitVariableExpression(VariableExpression expression) {
        VarReference reference = new VarReference(sourceFileURI, fileContents, currentClassNode, expression)
        finder.addVarUsage(reference)
        super.visitVariableExpression(expression)
    }

    @Override
    void visitWhileLoop(WhileStatement loop) {
        super.visitWhileLoop(loop)
    }
}
