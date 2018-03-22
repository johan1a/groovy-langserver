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

@Slf4j
@TypeChecked
class GroovyIndexer {

    String rootUri

    CompilePhase phase = CompilePhase.CLASS_GENERATION

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
        def astBuilder = new AstBuilder()
        log.info("${astBuilder}")
        List<ASTNode> astNodes = astBuilder.buildFromString( phase, false, file.text )
        astNodes.each { node ->
            log.info("node: ${node}")
            // log.info("statements: ${ast.getStatements()}")
            // log.info("methods: ${ast.getmethods()}")
        }
    }
}
