package org.gls.groovy

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.GroovyCodeVisitor
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.*
import org.codehaus.groovy.classgen.*
import org.codehaus.groovy.control.CompilationUnit
import org.codehaus.groovy.ast.ClassNode

@Slf4j
class ASTNodeVisitor implements GroovyCodeVisitor {

    void visit(ClassNode node) {
        log.debug "Visiting $node"

    }

    @Override
    void visitArgumentlistExpression(ArgumentListExpression expression){

    }

    @Override
    void visitArrayExpression(ArrayExpression expression){

    }

    @Override
    void visitAssertStatement(AssertStatement statement){

    }

    @Override
    void visitAttributeExpression(AttributeExpression attributeExpression){

    }

    @Override
    void visitBinaryExpression(BinaryExpression expression){

    }

    @Override
    void visitBitwiseNegationExpression(BitwiseNegationExpression expression){

    }

    @Override
    void visitBlockStatement(BlockStatement statement){
        log.debug "Visiting blockStatement: ${statement}"
    }

    @Override
    void visitBooleanExpression(BooleanExpression expression){

    }

    @Override
    void visitBreakStatement(BreakStatement statement){

    }

    @Override
    void visitBytecodeExpression(BytecodeExpression expression){

    }

    @Override
    void visitCaseStatement(CaseStatement statement){

    }

    @Override
    void visitCastExpression(CastExpression expression){

    }

    @Override
    void visitCatchStatement(CatchStatement statement){

    }

    @Override
    void visitClassExpression(ClassExpression expression){

    }

    @Override
    void visitClosureExpression(ClosureExpression expression){

    }

    @Override
    void visitClosureListExpression(ClosureListExpression closureListExpression){

    }

    @Override
    void visitConstantExpression(ConstantExpression expression){

    }

    @Override
    void visitConstructorCallExpression(ConstructorCallExpression expression){

    }

    @Override
    void visitContinueStatement(ContinueStatement statement){

    }

    @Override
    void visitDeclarationExpression(DeclarationExpression expression){

    }

    @Override
    void visitDoWhileLoop(DoWhileStatement loop){

    }

    @Override
    void visitExpressionStatement(ExpressionStatement statement){

    }

    @Override
    void visitFieldExpression(FieldExpression expression){

    }

    @Override
    void visitForLoop(ForStatement forLoop){

    }

    @Override
    void visitGStringExpression(GStringExpression expression){

    }

    @Override
    void visitIfElse(IfStatement ifElse){

    }

    @Override
    void visitListExpression(ListExpression expression){

    }

    @Override
    void visitMapEntryExpression(MapEntryExpression expression){

    }

    @Override
    void visitMapExpression(MapExpression expression){

    }

    @Override
    void visitMethodCallExpression(MethodCallExpression call){

    }

    @Override
    void visitMethodPointerExpression(MethodPointerExpression expression){

    }

    @Override
    void visitNotExpression(NotExpression expression){

    }

    @Override
    void visitPostfixExpression(PostfixExpression expression){

    }

    @Override
    void visitPrefixExpression(PrefixExpression expression){

    }

    @Override
    void visitPropertyExpression(PropertyExpression expression){

    }

    @Override
    void visitRangeExpression(RangeExpression expression){

    }

    @Override
    void visitReturnStatement(ReturnStatement statement){

    }

    @Override
    void visitShortTernaryExpression(ElvisOperatorExpression expression){

    }

    @Override
    void visitSpreadExpression(SpreadExpression expression){

    }

    @Override
    void visitSpreadMapExpression(SpreadMapExpression expression){

    }

    @Override
    void visitStaticMethodCallExpression(StaticMethodCallExpression expression){

    }

    @Override
    void visitSwitch(SwitchStatement statement){

    }

    @Override
    void visitSynchronizedStatement(SynchronizedStatement statement){

    }

    @Override
    void visitTernaryExpression(TernaryExpression expression){

    }

    @Override
    void visitThrowStatement(ThrowStatement statement){

    }

    @Override
    void visitTryCatchFinally(TryCatchStatement finally1){

    }

    @Override
    void visitTupleExpression(TupleExpression expression){

    }

    @Override
    void visitUnaryMinusExpression(UnaryMinusExpression expression){

    }

    @Override
    void visitUnaryPlusExpression(UnaryPlusExpression expression){

    }

    @Override
    void visitVariableExpression(VariableExpression expression){

    }

    @Override
    void visitWhileLoop(WhileStatement loop){

    }
}
