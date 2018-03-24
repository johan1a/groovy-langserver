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
import org.gls.lang.ReferenceStorage
import org.codehaus.groovy.ast.*
import org.gls.lang.ClassDefinition
import groovy.transform.TypeChecked

@Slf4j
@TypeChecked
class CodeVisitor implements GroovyCodeVisitor {

    private ReferenceStorage storage
    private String sourceFileURI

    CodeVisitor(ReferenceStorage storage, String sourceFileURI) {
        this.storage = storage
        this.sourceFileURI = sourceFileURI
    }

    void visit(ClassNode node) {
        log.info "Visiting class $node"
        storage.addClassDefinition(new ClassDefinition(node, sourceFileURI))
    }

    @Override
    void visitArgumentlistExpression(ArgumentListExpression expression){
        log.info "Visiting $expression"
    }

    @Override
    void visitArrayExpression(ArrayExpression expression){
        log.info "Visiting $expression"
    }

    @Override
    void visitAssertStatement(AssertStatement statement){
        log.info "Visiting $statement"
    }

    @Override
    void visitAttributeExpression(AttributeExpression attributeExpression){
        log.info "Visiting $attributeExpression"
    }

    @Override
    void visitBinaryExpression(BinaryExpression expression){
        log.info "Visiting $expression"
    }

    @Override
    void visitBitwiseNegationExpression(BitwiseNegationExpression expression){
        log.info "Visiting $expression"
    }

    @Override
    void visitBlockStatement(BlockStatement statement){
        log.info "Visiting $statement"
    }

    @Override
    void visitBooleanExpression(BooleanExpression expression){
        log.info "Visiting $expression"
    }

    @Override
    void visitBreakStatement(BreakStatement statement){
        log.info "Visiting $statement"
    }

    @Override
    void visitBytecodeExpression(BytecodeExpression expression){
        log.info "Visiting $expression"
    }

    @Override
    void visitCaseStatement(CaseStatement statement){
        log.info "Visiting $statement"
    }

    @Override
    void visitCastExpression(CastExpression expression){
        log.info "Visiting $expression"
    }

    @Override
    void visitCatchStatement(CatchStatement statement){
        log.info "Visiting $statement"
    }

    @Override
    void visitClassExpression(ClassExpression expression){
        log.info "Visiting $expression"
    }

    @Override
    void visitClosureExpression(ClosureExpression expression){
        log.info "Visiting $expression"
    }

    @Override
    void visitClosureListExpression(ClosureListExpression closureListExpression){
        log.info "Visiting $closureListExpression"
    }

    @Override
    void visitConstantExpression(ConstantExpression expression){
        log.info "Visiting $expression"
    }

    @Override
    void visitConstructorCallExpression(ConstructorCallExpression expression){
        log.info "Visiting $expression"
    }

    @Override
    void visitContinueStatement(ContinueStatement statement){
        log.info "Visiting $statement"
    }

    @Override
    void visitDeclarationExpression(DeclarationExpression expression){
        log.info "Visiting $expression"
    }

    @Override
    void visitDoWhileLoop(DoWhileStatement loop){
        log.info "Visiting $loop"
    }

    @Override
    void visitExpressionStatement(ExpressionStatement statement){
        log.info "Visiting $statement"
    }

    @Override
    void visitFieldExpression(FieldExpression expression){
        log.info "Visiting $expression"
    }

    @Override
    void visitForLoop(ForStatement forLoop){
        log.info "Visiting $forLoop"
    }

    @Override
    void visitGStringExpression(GStringExpression expression){
        log.info "Visiting $expression"
    }

    @Override
    void visitIfElse(IfStatement ifElse){
        log.info "Visiting $ifElse"
    }

    @Override
    void visitListExpression(ListExpression expression){
        log.info "Visiting $expression"
    }

    @Override
    void visitMapEntryExpression(MapEntryExpression expression){
        log.info "Visiting $expression"
    }

    @Override
    void visitMapExpression(MapExpression expression){
        log.info "Visiting $expression"
    }

    @Override
    void visitMethodCallExpression(MethodCallExpression call){
        log.info "Visiting $call"
    }

    @Override
    void visitMethodPointerExpression(MethodPointerExpression expression){
        log.info "Visiting $expression"
    }

    @Override
    void visitNotExpression(NotExpression expression){
        log.info "Visiting $expression"
    }

    @Override
    void visitPostfixExpression(PostfixExpression expression){
        log.info "Visiting $expression"
    }

    @Override
    void visitPrefixExpression(PrefixExpression expression){
        log.info "Visiting $expression"
    }

    @Override
    void visitPropertyExpression(PropertyExpression expression){
        log.info "Visiting $expression"
    }

    @Override
    void visitRangeExpression(RangeExpression expression){
        log.info "Visiting $expression"
    }

    @Override
    void visitReturnStatement(ReturnStatement statement){
        log.info "Visiting $statement"
    }

    @Override
    void visitShortTernaryExpression(ElvisOperatorExpression expression){
        log.info "Visiting $expression"
    }

    @Override
    void visitSpreadExpression(SpreadExpression expression){
        log.info "Visiting $expression"
    }

    @Override
    void visitSpreadMapExpression(SpreadMapExpression expression){
        log.info "Visiting $expression"
    }

    @Override
    void visitStaticMethodCallExpression(StaticMethodCallExpression expression){
        log.info "Visiting $expression"
    }

    @Override
    void visitSwitch(SwitchStatement statement){
        log.info "Visiting $statement"
    }

    @Override
    void visitSynchronizedStatement(SynchronizedStatement statement){
        log.info "Visiting $statement"
    }

    @Override
    void visitTernaryExpression(TernaryExpression expression){
        log.info "Visiting $expression"
    }

    @Override
    void visitThrowStatement(ThrowStatement statement){
        log.info "Visiting $statement"
    }

    @Override
    void visitTryCatchFinally(TryCatchStatement finally1){
        log.info "Visiting $finally1"
    }

    @Override
    void visitTupleExpression(TupleExpression expression){
        log.info "Visiting $expression"
    }

    @Override
    void visitUnaryMinusExpression(UnaryMinusExpression expression){
        log.info "Visiting $expression"
    }

    @Override
    void visitUnaryPlusExpression(UnaryPlusExpression expression){
        log.info "Visiting $expression"
    }

    @Override
    void visitVariableExpression(VariableExpression expression){
        log.info "Visiting $expression"
    }

    @Override
    void visitWhileLoop(WhileStatement loop){
        log.info "Visiting $loop"
    }
}
