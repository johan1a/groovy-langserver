package org.gls.groovy

import groovy.util.logging.Slf4j
import groovy.transform.TypeChecked
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.stmt.*
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.control.CompilationUnit
import org.codehaus.groovy.ast.*
import org.gls.lang.ReferenceStorage

@Slf4j
@TypeChecked
class GroovyIndexer {

    String rootUri

    CompilePhase phase = CompilePhase.CLASS_GENERATION
    ReferenceStorage storage

    GroovyIndexer(String rootUri, ReferenceStorage storage) {
        this.rootUri = rootUri
        this.storage = storage
    }

    def startIndexing() {
      try {
        CompilationUnit unit = new CompilationUnit()
        long start = System.currentTimeMillis()
        File basedir = new File(new URL(rootUri).toURI())
        basedir.eachFileRecurse {
          if (it.name =~ /.*\.groovy/) {
              unit.addSource(it)
          }
        }
        unit.compile()

        List<ClassNode> classes = unit.getClasses()

        unit.iterator().each { sourceUnit ->
            ModuleNode moduleNode = sourceUnit.getAST()
            CodeVisitor codeVisitor = new CodeVisitor(storage, sourceUnit.getName())
            moduleNode.visit(codeVisitor)
            moduleNode.getClasses().each { classNode ->
                codeVisitor.visitClass(classNode)
            }
        }

        long elapsed = System.currentTimeMillis() - start
        log.info("Elapsed: ${elapsed / 1000}s")
      } catch(Exception e) {
        log.error("error", e)
      }
    }
}
