package org.gls.groovy

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.codehaus.groovy.ast.ModuleNode
import org.codehaus.groovy.control.CompilationUnit
import org.codehaus.groovy.control.Phases
import org.gls.lang.ReferenceStorage

@Slf4j
@TypeChecked
class GroovyIndexer {

    URI rootUri
    ReferenceStorage storage

    URI getRootUri() {
        return rootUri
    }

    GroovyIndexer(URI rootUri, ReferenceStorage storage) {
        this.rootUri = rootUri
        this.storage = storage
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

            īndexFiles(files)

            long elapsed = System.currentTimeMillis() - start
            log.info("Indexing done in ${elapsed / 1000}s")
        } catch (Exception e) {
            log.error("error", e)
        }
    }

    private void īndexFiles(List<File> files) {
        CompilationUnit unit = new CompilationUnit()
        files.each { unit.addSource(it) }

        unit.compile(Phases.CANONICALIZATION)

        unit.iterator().each { sourceUnit ->
            ModuleNode moduleNode = sourceUnit.getAST()
            CodeVisitor codeVisitor = new CodeVisitor(storage, sourceUnit.getName())
            moduleNode.visit(codeVisitor)
            moduleNode.getClasses().each { classNode ->
                codeVisitor.visitClass(classNode)
            }
        }
    }
}
