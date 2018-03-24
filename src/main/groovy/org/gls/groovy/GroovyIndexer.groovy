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
    ClassVisitor classVisitor = new ClassVisitor()
    ReferenceStorage storage = new ReferenceStorage()

    def startIndexing() {
      try {
        log.info "Indexing..."
        CompilationUnit unit = new CompilationUnit()
        long start = System.currentTimeMillis()
        File basedir = new File(new URL(rootUri).toURI())
        log.info "baseDir: ${basedir}"
        basedir.eachFileRecurse {
          if (it.name =~ /.*\.groovy/) {
              unit.addSource(it)
          }
        }
        unit.compile()

        List<ClassNode> classes = unit.getClasses()
        log.info "Classes: ${classes}"

        unit.iterator().each { sourceUnit ->
            ModuleNode moduleNode = sourceUnit.getAST()
            log.info("SourceUnit name: ${sourceUnit.getName()}")
            CodeVisitor codeVisitor = new CodeVisitor(storage, sourceUnit.getName())
            moduleNode.visit(codeVisitor)
            moduleNode.getClasses().each { classNode ->
                codeVisitor.visit(classNode)
            }
        }

        long elapsed = System.currentTimeMillis() - start
        log.info("Elapsed: ${elapsed / 1000}s")
      } catch(Exception e) {
        log.error("error", e)
      }
    }

    def indexFile(File file, CompilationUnit unit) {
        log.info file.name
    }
}
