package org.gls.groovy

import groovy.transform.TypeChecked
import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.GroovyCodeVisitor
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.*
import org.codehaus.groovy.classgen.*
import org.codehaus.groovy.control.CompilationUnit
import org.codehaus.groovy.control.SourceUnit
import org.gls.lang.*
import org.gls.lang.ReferenceStorage

@Slf4j
@TypeChecked
class CodeVisitor extends ClassCodeVisitorSupport {

    private ReferenceStorage storage
    private String sourceFileURI
    String heya = "WHOA"

    CodeVisitor(ReferenceStorage storage, String sourceFileURI) {
        this.storage = storage
        this.sourceFileURI = sourceFileURI
    }

    @Override
    SourceUnit getSourceUnit() {
        throw new Exception("Not implemented")
    }

    @Override
    void visitClass(ClassNode node) {
        log.info "Visiting ClassNode $node"
        storage.addClassDefinition(new ClassDefinition(node, sourceFileURI))
        super.visitClass(node)
    }

    @Override
    void visitConstructor(ConstructorNode node){
        log.info "Visiting ConstructorNode $node"
        super.visitConstructor(node)
    }

    @Override
    void visitField(FieldNode node){
        log.info "Visiting field: $node"
        storage.addClassReference(new ClassReference(sourceFileURI, node))
        storage.addVarDefinition(new VarDefinition(sourceFileURI, node))
        super.visitField(node)
    }

    @Override
    void visitMethod(MethodNode node){
        log.info "visiting MethodNode $node"
        super.visitMethod(node)
    }

    @Override
    void visitProperty(PropertyNode node){
        log.info "visiting PropertyNode $node"
        super.visitProperty(node)
    }

    @Override
    void visitArgumentlistExpression(ArgumentListExpression expression){
        log.info "Visiting $expression"
        super.visitArgumentlistExpression(expression)
    }

    @Override
    void visitArrayExpression(ArrayExpression expression){
        log.info "Visiting $expression"
        super.visitArrayExpression(expression)
    }

    @Override
    void visitAssertStatement(AssertStatement statement){
        log.info "Visiting $statement"
        super.visitAssertStatement(statement)
    }

    @Override
    void visitAttributeExpression(AttributeExpression attributeExpression){
        log.info "Visiting $attributeExpression"
        super.visitAttributeExpression(attributeExpression)
    }

    @Override
    void visitBinaryExpression(BinaryExpression expression){
        log.info "Visiting $expression"
        super.visitBinaryExpression(expression)
    }

    @Override
    void visitBitwiseNegationExpression(BitwiseNegationExpression expression){
        log.info "Visiting $expression"
        super.visitBitwiseNegationExpression(expression)
    }

    @Override
    void visitBlockStatement(BlockStatement statement){
        log.info "Visiting $statement"
        super.visitBlockStatement(statement)
    }

    @Override
    void visitBooleanExpression(BooleanExpression expression){
        log.info "Visiting $expression"
        super.visitBooleanExpression(expression)
    }

    @Override
    void visitBreakStatement(BreakStatement statement){
        log.info "Visiting $statement"
        super.visitBreakStatement(statement)
    }

    @Override
    void visitBytecodeExpression(BytecodeExpression expression){
        log.info "Visiting $expression"
        super.visitBytecodeExpression(expression)
    }

    @Override
    void visitCaseStatement(CaseStatement statement){
        log.info "Visiting $statement"
        super.visitCaseStatement(statement)
    }

    @Override
    void visitCastExpression(CastExpression expression){
        log.info "Visiting $expression"
        super.visitCastExpression(expression)
    }

    @Override
    void visitCatchStatement(CatchStatement statement){
        log.info "Visiting $statement"
        super.visitCatchStatement(statement)
    }

    @Override
    void visitClassExpression(ClassExpression expression){
        log.info "Visiting $expression"
        super.visitClassExpression(expression)
    }

    @Override
    void visitClosureExpression(ClosureExpression expression){
        log.info "Visiting $expression"
        super.visitClosureExpression(expression)
    }

    @Override
    void visitClosureListExpression(ClosureListExpression closureListExpression){
        log.info "Visiting $closureListExpression"
        super.visitClosureListExpression(closureListExpression)
    }

    @Override
    void visitConstantExpression(ConstantExpression expression){
        log.info "Visiting $expression"
        super.visitConstantExpression(expression)
    }

    @Override
    void visitConstructorCallExpression(ConstructorCallExpression expression){
        log.info "Visiting $expression"
        super.visitConstructorCallExpression(expression)
    }

    @Override
    void visitContinueStatement(ContinueStatement statement){
        log.info "Visiting $statement"
        super.visitContinueStatement(statement)
    }

    @Override
    void visitDeclarationExpression(DeclarationExpression expression){
        log.info "Visiting $expression"
        super.visitDeclarationExpression(expression)
    }

    @Override
    void visitDoWhileLoop(DoWhileStatement loop){
        log.info "Visiting $loop"
        super.visitDoWhileLoop(loop)
    }

    @Override
    void visitExpressionStatement(ExpressionStatement statement){
        log.info "Visiting $statement"
        super.visitExpressionStatement(statement)
    }

    @Override
    void visitFieldExpression(FieldExpression expression){
        log.info "Visiting $expression"
        super.visitFieldExpression(expression)
    }

    @Override
    void visitForLoop(ForStatement forLoop){
        log.info "Visiting $forLoop"
        super.visitForLoop(forLoop)
    }

    @Override
    void visitGStringExpression(GStringExpression expression){
        log.info "Visiting $expression"
        super.visitGStringExpression(expression)
    }

    @Override
    void visitIfElse(IfStatement ifElse){
        log.info "Visiting $ifElse"
        super.visitIfElse(ifElse)
    }

    @Override
    void visitListExpression(ListExpression expression){
        log.info "Visiting $expression"
        super.visitListExpression(expression)
    }

    @Override
    void visitMapEntryExpression(MapEntryExpression expression){
        log.info "Visiting $expression"
        super.visitMapEntryExpression(expression)
    }

    @Override
    void visitMapExpression(MapExpression expression){
        log.info "Visiting $expression"
        super.visitMapExpression(expression)
    }

    @Override
    void visitMethodCallExpression(MethodCallExpression call){
        log.info "Visiting $call"
        super.visitMethodCallExpression(call)
    }

    @Override
    void visitMethodPointerExpression(MethodPointerExpression expression){
        log.info "Visiting $expression"
        super.visitMethodPointerExpression(expression)
    }

    @Override
    void visitNotExpression(NotExpression expression){
        log.info "Visiting $expression"
        super.visitNotExpression(expression)
    }

    @Override
    void visitPostfixExpression(PostfixExpression expression){
        log.info "Visiting $expression"
        super.visitPostfixExpression(expression)
    }

    @Override
    void visitPrefixExpression(PrefixExpression expression){
        log.info "Visiting $expression"
        super.visitPrefixExpression(expression)
    }

    @Override
    void visitPropertyExpression(PropertyExpression expression){
        log.info "Visiting $expression"
        super.visitPropertyExpression(expression)
    }

    @Override
    void visitRangeExpression(RangeExpression expression){
        log.info "Visiting $expression"
        super.visitRangeExpression(expression)
    }

    @Override
    void visitReturnStatement(ReturnStatement statement){
        log.info "Visiting $statement"
        super.visitReturnStatement(statement)
    }

    @Override
    void visitShortTernaryExpression(ElvisOperatorExpression expression){
        log.info "Visiting $expression"
        super.visitShortTernaryExpression(expression)
    }

    @Override
    void visitSpreadExpression(SpreadExpression expression){
        log.info "Visiting $expression"
        super.visitSpreadExpression(expression)
    }

    @Override
    void visitSpreadMapExpression(SpreadMapExpression expression){
        log.info "Visiting $expression"
        super.visitSpreadMapExpression(expression)
    }

    @Override
    void visitStaticMethodCallExpression(StaticMethodCallExpression expression){
        log.info "Visiting $expression"
        super.visitStaticMethodCallExpression(expression)
    }

    @Override
    void visitSwitch(SwitchStatement statement){
        log.info "Visiting $statement"
        super.visitSwitch(statement)
    }

    @Override
    void visitSynchronizedStatement(SynchronizedStatement statement){
        log.info "Visiting $statement"
        super.visitSynchronizedStatement(statement)
    }

    @Override
    void visitTernaryExpression(TernaryExpression expression){
        log.info "Visiting $expression"
        super.visitTernaryExpression(expression)
    }

    @Override
    void visitThrowStatement(ThrowStatement statement){
        log.info "Visiting $statement"
        super.visitThrowStatement(statement)
    }

    @Override
    void visitTryCatchFinally(TryCatchStatement finally1){
        log.info "Visiting $finally1"
        super.visitTryCatchFinally(finally1)
    }

    @Override
    void visitTupleExpression(TupleExpression expression){
        log.info "Visiting $expression"
        super.visitTupleExpression(expression)
    }

    @Override
    void visitUnaryMinusExpression(UnaryMinusExpression expression){
        log.info "Visiting $expression"
        super.visitUnaryMinusExpression(expression)
    }

    @Override
    void visitUnaryPlusExpression(UnaryPlusExpression expression){
        log.info "Visiting $expression"
        super.visitUnaryPlusExpression(expression)
    }

    @Override
    void visitVariableExpression(VariableExpression expression){
        log.info "Visiting $expression"
        super.visitVariableExpression(expression)
    }

    @Override
    void visitWhileLoop(WhileStatement loop){
        log.info "Visiting $loop"
        super.visitWhileLoop(loop)
    }
}
