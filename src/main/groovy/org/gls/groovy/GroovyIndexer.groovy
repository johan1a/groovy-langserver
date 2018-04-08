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

import java.util.concurrent.ExecutionException


@Slf4j
@TypeChecked
class GroovyIndexer {

    List<URI> sourcePaths

    ReferenceFinder finder = new ReferenceFinder()


    List<URI> getRootUri() {
        return sourcePaths
    }

    GroovyIndexer(List<URI> sourcePaths, ReferenceFinder finder) {
        this.sourcePaths = sourcePaths
        this.finder = finder
    }

    Map<String, List<Diagnostic>> index(Map<String, String> changedFiles = [:]) {
        List<File> files = findFilesRecursive()
        return index(files, changedFiles)
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
                log.debug("Source dir not found", e)
            }
        }
        return files
    }

    Map<String, List<Diagnostic>> index(List<File> files, Map<String, String> changedFiles) {
        Map<String, List<Diagnostic>> diagnostics = new HashMap<>()
        try {
            long start = System.currentTimeMillis()
            compile(files, changedFiles)
            long elapsed = System.currentTimeMillis() - start
            log.info("Indexing done in ${elapsed / 1000}s")
        } catch (MultipleCompilationErrorsException e) {
            diagnostics = getDiagnostics(e.getErrorCollector())
        }
        log.info("diagnostics: ${diagnostics}")
        return diagnostics
    }

    private void compile(List<File> files, Map<String, String> changedFiles) {
        List<File> notChanged = files.findAll { !changedFiles.keySet().contains(it.canonicalPath) }

        CompilationUnit unit = new CompilationUnit()
        notChanged.each { unit.addSource(it) }
        changedFiles.each { path, name -> unit.addSource(path, name) }

        unit.compile(Phases.CANONICALIZATION)

        unit.iterator().each { sourceUnit ->
            ModuleNode moduleNode = sourceUnit.getAST()

            String name = sourceUnit.getName()
            List<String> fileContents = getFileContent(name, changedFiles)
            CodeVisitor codeVisitor = new CodeVisitor(finder, name, fileContents)
            moduleNode.visit(codeVisitor)
            moduleNode.getClasses().each { classNode ->
                codeVisitor.visitClass(classNode)
            }
        }
    }

    static List<String> getFileContent(String fileName, Map<String, String> changedFiles) {
        if(changedFiles.containsKey(fileName)){
            return changedFiles.get(fileName).split('\n').toList() //TODO proper line end split
        }
        return new File(fileName).readLines()
    }

    private static Map<String, List<Diagnostic>> getDiagnostics(ErrorCollector errorCollector) {
        Map<String, List<Diagnostic>> diagnosticMap = new HashMap<>()
        try {
            if (errorCollector == null) {
                return diagnosticMap
            }
            List<SyntaxErrorMessage> errors = errorCollector.getErrors()
            List<Message> warnings = errorCollector.getWarnings()
            errors?.each {
                SyntaxException exception = it.getCause()
                String uri = "file://" + exception.getSourceLocator()
                Diagnostic diagnostic = asDiagnostic(exception)

                List<Diagnostic> diagnostics = diagnosticMap.get(uri)
                if (diagnostics == null) {
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

    private static Diagnostic asDiagnostic(SyntaxException exception) {
        int line = exception.getLine() - 1
        Position start = new Position(line, exception.getStartColumn())
        Position end = new Position(line, exception.getEndColumn())
        Range range = new Range(start, end)

        Diagnostic diagnostic = new Diagnostic(range, exception.getMessage(), DiagnosticSeverity.Error, "Groovy")
        return diagnostic
    }

}
