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
import org.eclipse.lsp4j.*
import org.codehaus.groovy.syntax.*
import org.codehaus.groovy.control.messages.*

@Slf4j
@TypeChecked
class GroovyIndexer {

    List<URI> sourcePaths
    ReferenceFinder finder
    List<File> allFiles

    List<URI> getRootUri() {
        return sourcePaths
    }

    ErrorCollector getErrorCollector() {
        return errorCollector
    }

    GroovyIndexer(List<URI> sourcePaths, ReferenceFinder finder) {
        this.sourcePaths = sourcePaths
        this.finder = finder
    }

    Map<String, List<Diagnostic> > index() {
        List<File> files = findFilesRecursive()
        return index(files)
    }

    List<File> findFilesRecursive() {
        List<File> files = new LinkedList<>()
        sourcePaths.each {
            try {
                File basedir = new File(it)
                basedir.eachFileRecurse {
                    if (it.name =~ /.*\.groovy/) {
                        files.add(it)
                    }
                }
            } catch (FileNotFoundException e) {
                log.error("Error", e)
            }
        }
        return files
    }

    Map<String, List<Diagnostic> > index(List<File> files) {
        Map<String, List<Diagnostic>> diagnostics = new HashMap<>()
        try {
            long start = System.currentTimeMillis()
            this.allFiles = files
            compile(files)
            long elapsed = System.currentTimeMillis() - start
            log.info("Indexing done in ${elapsed / 1000}s")
        } catch (MultipleCompilationErrorsException e) {
            diagnostics = getDiagnostics(e.getErrorCollector())
        } catch (Exception e) {
            log.error("error", e)
        }
        log.info("diagnostics: ${diagnostics}")
        return diagnostics
    }

    private void compile(List<File> files) {
        CompilationUnit unit = new CompilationUnit()
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

    private Map<String, List<Diagnostic> > getDiagnostics(ErrorCollector errorCollector) {
        Map<String, List<Diagnostic> > diagnosticMap = new HashMap<>()
        try {
            if(errorCollector == null) {
                return diagnosticMap
            }
            List<SyntaxErrorMessage> errors = errorCollector.getErrors()
            List<Message> warnings = errorCollector.getWarnings()
            errors?.each {
                SyntaxException exception = it.getCause()
                String uri = "file://" + exception.getSourceLocator()
                Diagnostic diagnostic = asDiagnostic(exception)

                List<Diagnostic> diagnostics = diagnosticMap.get(uri)
                if(diagnostics == null) {
                    diagnostics = new LinkedList<>()
                    diagnosticMap.put(uri, diagnostics)
                }
                diagnostics.add(diagnostic)
            }
            warnings?.each {
                log.info it.toString()
                log.info "TODO implement warning diagnostics"
            }
        } catch (Exception e) {
            log.error("Error", e)
        }
        return diagnosticMap
    }

    private Diagnostic asDiagnostic(SyntaxException exception) {
        int line = exception.getLine() - 1
        Position start = new Position(line, exception.getStartColumn())
        Position end = new Position(line, exception.getEndColumn())
        Range range = new Range(start, end)

        Diagnostic diagnostic = new Diagnostic(range, exception.getMessage())
        return diagnostic
    }

}
