package org.gls.groovy

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.*
import org.codehaus.groovy.classgen.*
import org.codehaus.groovy.control.SourceUnit
import org.gls.lang.*
import org.gls.lang.ReferenceStorage

@Slf4j
@TypeChecked
class CodeVisitor extends ClassCodeVisitorSupport {

    private ReferenceFinder finder
    private String sourceFileURI
    private ClassNode currentClassNode

    CodeVisitor(ReferenceFinder finder, String sourceFileURI) {
        this.finder = finder
        this.sourceFileURI = sourceFileURI
    }

    @Override
    SourceUnit getSourceUnit() {
        throw new Exception("Not implemented")
    }

    @Override
    void visitClass(ClassNode node) {
        currentClassNode = node
        finder.addClassDefinition(new ClassDefinition(node, sourceFileURI))
        super.visitClass(node)
    }

    @Override
    void visitConstructor(ConstructorNode node){
        super.visitConstructor(node)
    }

    @Override
    void visitField(FieldNode node){
        finder.addClassUsage(new ClassUsage(sourceFileURI, node))
        finder.addVarDefinition(new VarDefinition(sourceFileURI, node))
        super.visitField(node)
    }

    @Override
    void visitMethod(MethodNode node){
        finder.addClassUsage(new ClassUsage(sourceFileURI, node))
        finder.addFuncDefinition(sourceFileURI, new FuncDefinition(sourceFileURI, currentClassNode.getName(), node))
        node.parameters.each { Parameter it ->
            finder.addVarDefinition( new VarDefinition(sourceFileURI, it))
            finder.addClassUsage(new ClassUsage(sourceFileURI, it))
        }
        super.visitMethod(node)
    }

    @Override
    void visitProperty(PropertyNode node){
        super.visitProperty(node)
    }

    @Override
    void visitArgumentlistExpression(ArgumentListExpression expression){
        super.visitArgumentlistExpression(expression)
    }

    @Override
    void visitArrayExpression(ArrayExpression expression){
        super.visitArrayExpression(expression)
    }

    @Override
    void visitAssertStatement(AssertStatement statement){
        super.visitAssertStatement(statement)
    }

    @Override
    void visitAttributeExpression(AttributeExpression attributeExpression){
        super.visitAttributeExpression(attributeExpression)
    }

    @Override
    void visitBinaryExpression(BinaryExpression expression){
        super.visitBinaryExpression(expression)
    }

    @Override
    void visitBitwiseNegationExpression(BitwiseNegationExpression expression){
        super.visitBitwiseNegationExpression(expression)
    }

    @Override
    void visitBlockStatement(BlockStatement statement){
        super.visitBlockStatement(statement)
    }

    @Override
    void visitBooleanExpression(BooleanExpression expression){
        super.visitBooleanExpression(expression)
    }

    @Override
    void visitBreakStatement(BreakStatement statement){
        super.visitBreakStatement(statement)
    }

    @Override
    void visitBytecodeExpression(BytecodeExpression expression){
        super.visitBytecodeExpression(expression)
    }

    @Override
    void visitCaseStatement(CaseStatement statement){
        super.visitCaseStatement(statement)
    }

    @Override
    void visitCastExpression(CastExpression expression){
        super.visitCastExpression(expression)
    }

    @Override
    void visitCatchStatement(CatchStatement statement){
        super.visitCatchStatement(statement)
    }

    @Override
    void visitClassExpression(ClassExpression expression){
        super.visitClassExpression(expression)
    }

    @Override
    void visitClosureExpression(ClosureExpression expression){
        super.visitClosureExpression(expression)
    }

    @Override
    void visitClosureListExpression(ClosureListExpression closureListExpression){
        super.visitClosureListExpression(closureListExpression)
    }

    @Override
    void visitConstantExpression(ConstantExpression expression){
        super.visitConstantExpression(expression)
    }

    @Override
    void visitConstructorCallExpression(ConstructorCallExpression expression){
        super.visitConstructorCallExpression(expression)
    }

    @Override
    void visitContinueStatement(ContinueStatement statement){
        super.visitContinueStatement(statement)
    }

    @Override
    void visitDeclarationExpression(DeclarationExpression expression){
        if(expression.isMultipleAssignmentDeclaration()) {
            TupleExpression left = expression.getTupleExpression()
        } else {
            VariableExpression left = expression.getVariableExpression()
            finder.addVarDefinition(new VarDefinition(sourceFileURI, left))
            finder.addClassUsage(new ClassUsage(sourceFileURI, expression))
        }
        super.visitDeclarationExpression(expression)
    }

    @Override
    void visitDoWhileLoop(DoWhileStatement loop){
        super.visitDoWhileLoop(loop)
    }

    @Override
    void visitExpressionStatement(ExpressionStatement statement){
        super.visitExpressionStatement(statement)
    }

    @Override
    void visitFieldExpression(FieldExpression expression){
        super.visitFieldExpression(expression)
    }

    @Override
    void visitForLoop(ForStatement forLoop){
        super.visitForLoop(forLoop)
    }

    @Override
    void visitGStringExpression(GStringExpression expression){
        super.visitGStringExpression(expression)
    }

    @Override
    void visitIfElse(IfStatement ifElse){
        super.visitIfElse(ifElse)
    }

    @Override
    void visitListExpression(ListExpression expression){
        super.visitListExpression(expression)
    }

    @Override
    void visitMapEntryExpression(MapEntryExpression expression){
        super.visitMapEntryExpression(expression)
    }

    @Override
    void visitMapExpression(MapExpression expression){
        super.visitMapExpression(expression)
    }

    @Override
    void visitMethodCallExpression(MethodCallExpression call){
         
        super.visitMethodCallExpression(call)
    }

    @Override
    void visitMethodPointerExpression(MethodPointerExpression expression){
        super.visitMethodPointerExpression(expression)
    }

    @Override
    void visitNotExpression(NotExpression expression){
        super.visitNotExpression(expression)
    }

    @Override
    void visitPostfixExpression(PostfixExpression expression){
        super.visitPostfixExpression(expression)
    }

    @Override
    void visitPrefixExpression(PrefixExpression expression){
        super.visitPrefixExpression(expression)
    }

    @Override
    void visitPropertyExpression(PropertyExpression expression){
        super.visitPropertyExpression(expression)
    }

    @Override
    void visitRangeExpression(RangeExpression expression){
        super.visitRangeExpression(expression)
    }

    @Override
    void visitReturnStatement(ReturnStatement statement){
        super.visitReturnStatement(statement)
    }

    @Override
    void visitShortTernaryExpression(ElvisOperatorExpression expression){
        super.visitShortTernaryExpression(expression)
    }

    @Override
    void visitSpreadExpression(SpreadExpression expression){
        super.visitSpreadExpression(expression)
    }

    @Override
    void visitSpreadMapExpression(SpreadMapExpression expression){
        super.visitSpreadMapExpression(expression)
    }

    @Override
    void visitStaticMethodCallExpression(StaticMethodCallExpression expression){
        super.visitStaticMethodCallExpression(expression)
    }

    @Override
    void visitSwitch(SwitchStatement statement){
        super.visitSwitch(statement)
    }

    @Override
    void visitSynchronizedStatement(SynchronizedStatement statement){
        super.visitSynchronizedStatement(statement)
    }

    @Override
    void visitTernaryExpression(TernaryExpression expression){
        super.visitTernaryExpression(expression)
    }

    @Override
    void visitThrowStatement(ThrowStatement statement){
        super.visitThrowStatement(statement)
    }

    @Override
    void visitTryCatchFinally(TryCatchStatement finally1){
        super.visitTryCatchFinally(finally1)
    }

    @Override
    void visitTupleExpression(TupleExpression expression){
        super.visitTupleExpression(expression)
    }

    @Override
    void visitUnaryMinusExpression(UnaryMinusExpression expression){
        super.visitUnaryMinusExpression(expression)
    }

    @Override
    void visitUnaryPlusExpression(UnaryPlusExpression expression){
        super.visitUnaryPlusExpression(expression)
    }

    @Override
    void visitVariableExpression(VariableExpression expression){
        finder.addVarUsage(new VarUsage(sourceFileURI, currentClassNode, expression))
        super.visitVariableExpression(expression)
    }

    @Override
    void visitWhileLoop(WhileStatement loop){
        super.visitWhileLoop(loop)
    }
}
