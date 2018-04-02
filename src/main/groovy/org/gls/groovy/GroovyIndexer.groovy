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

    void index() {
        try {
            List<File> files = findFilesRecursive()
            index(files, unit)
        } catch (FileNotFoundException e) {
            log.error("Error", e)
        }
    }

    void index(List<File> files) {
        index(files, unit)
    }

    List<File> findFilesRecursive() {
        File basedir = new File(rootUri)

        List<File> files = new LinkedList<>()
        basedir.eachFileRecurse {
            if (it.name =~ /.*\.groovy/) {
                files.add(it)
            }
        }
        return files
    }

    void index(List<File> files, CompilationUnit unit) {
        try {
            long start = System.currentTimeMillis()
            compile(files, unit)
            long elapsed = System.currentTimeMillis() - start
            log.info("Indexing done in ${elapsed / 1000}s")
        } catch (MultipleCompilationErrorsException e) {
            this.errorCollector = e.getErrorCollector()
        } catch (Exception e) {
            log.error("error", e)
        }
    }

    private void compile(List<File> files, CompilationUnit unit) {
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
