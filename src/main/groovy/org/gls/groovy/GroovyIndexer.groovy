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

@Slf4j
@TypeChecked
class GroovyIndexer {

    String rootUri

    CompilePhase phase = CompilePhase.CLASS_GENERATION
    ASTNodeVisitor visitor = new ASTNodeVisitor()

    def startIndexing() {
      try {
        log.info "Indexing..."
        long start = System.currentTimeMillis()
        File basedir = new File(new URL(rootUri).toURI())
        log.info "baseDir: ${basedir}"
        basedir.eachFileRecurse {
          if (it.name =~ /.*\.groovy/) {
            indexFile(it)
          }
        }
        long elapsed = System.currentTimeMillis() - start
        log.info("Elapsed: ${elapsed / 1000}s")
      } catch(Exception e) {
        log.error("error", e)
      }
    }

    def indexFile(File file) {
        log.info file.name
        CompilationUnit unit = new CompilationUnit()
        unit.addSource(file)
        unit.compile()
        log.info "Compiled $file"
        log.info "${unit.getClasses()}"
        unit.getClasses().each {
          visitor.visit(it)
        }
    }
}
