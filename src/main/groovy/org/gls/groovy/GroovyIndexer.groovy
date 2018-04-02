package org.gls.groovy

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.codehaus.groovy.ast.ModuleNode
import org.codehaus.groovy.control.CompilationUnit
import org.codehaus.groovy.control.Phases
import org.gls.lang.ReferenceFinder
import org.gls.lang.ReferenceStorage
import org.codehaus.groovy.control.ErrorCollector
import org.codehaus.groovy.control.MultipleCompilationErrorsException

@Slf4j
@TypeChecked
class GroovyIndexer {

    URI rootUri
    ReferenceFinder finder
    CompilationUnit unit = new CompilationUnit()
    ErrorCollector errorCollector

    URI getRootUri() {
        return rootUri
    }

    ErrorCollector getErrorCollector() {
        return errorCollector
    }

    GroovyIndexer(URI rootUri, ReferenceFinder finder) {
        this.rootUri = rootUri
        this.finder = finder
    }

    void indexRecursive() {
        try {
            long start = System.currentTimeMillis()
            File basedir = new File(rootUri)

            List<File> files = new LinkedList<>()
            basedir.eachFileRecurse {
                if (it.name =~ /.*\.groovy/) {
                    files.add(it)
                }
            }

            unit = new CompilationUnit()
            īndexFiles(unit, files)

            long elapsed = System.currentTimeMillis() - start
            log.info("Indexing done in ${elapsed / 1000}s")
        } catch (MultipleCompilationErrorsException e) {
            this.errorCollector = e.getErrorCollector()
        } catch (Exception e) {
            log.error("error", e)
        }
    }

    private void īndexFiles(CompilationUnit unit, List<File> files) {
        files.each { unit.addSource(it) }

        unit.compile(Phases.CANONICALIZATION)

        unit.iterator().each { sourceUnit ->
            ModuleNode moduleNode = sourceUnit.getAST()
            CodeVisitor codeVisitor = new CodeVisitor(finder, sourceUnit.getName())
            moduleNode.visit(codeVisitor)
            moduleNode.getClasses().each { classNode ->
                codeVisitor.visitClass(classNode)
            }
        }
    }
}
