package org.gls.groovy

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.codehaus.groovy.ast.ModuleNode
import org.codehaus.groovy.control.CompilationUnit
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.MultipleCompilationErrorsException
import org.codehaus.groovy.control.Phases
import org.eclipse.lsp4j.Diagnostic
import org.gls.ConfigService
import org.gls.UriUtils
import org.gls.lang.DiagnosticsParser
import org.gls.lang.ReferenceFinder

@Slf4j
@TypeChecked
class GroovyIndexer {

    List<URI> sourcePaths

    ReferenceFinder finder = new ReferenceFinder()
    URI rootUri
    ConfigService configService = new ConfigService()
    String buildConfigLocation = "build.gradle"

    GroovyIndexer(URI rootUri, ReferenceFinder finder, boolean scanAllSubDirs = false) {
        this.rootUri = rootUri
        sourcePaths = [UriUtils.appendURI(rootUri, "/src/main/groovy"),
                       UriUtils.appendURI(rootUri, "/grails-app")]
        if (scanAllSubDirs) {
            sourcePaths = [rootUri]
        }
        log.info "sourcePaths: ${sourcePaths}"

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
            log.info("Starting indexing")
            long start = System.currentTimeMillis()
            List<String> classpath = configService.resolveClassPath(rootUri, buildConfigLocation)
            compile(files, changedFiles, classpath)
            finder.correlate()
            long elapsed = System.currentTimeMillis() - start
            log.info("Indexing done in ${elapsed / 1000}s")
        } catch (MultipleCompilationErrorsException e) {
            diagnostics = DiagnosticsParser.getDiagnostics(e.getErrorCollector())
            if (diagnostics.isEmpty()) {
                log.error("Compilation error without diagnostics:", e)
            }
        } catch (NoClassDefFoundError e) {
            log.error("Compilation error:", e)
        }
        log.info("diagnostics: ${diagnostics}")
        return diagnostics
    }


    private void compile(List<File> files, Map<String, String> changedFiles, List<String> classpath) {
        log.info("compiling...")
        List<File> notChanged = files.findAll { !changedFiles.keySet().contains(it.canonicalPath) }

        CompilationUnit unit = new CompilationUnit()

        CompilerConfiguration configuration = new CompilerConfiguration()
        configuration.setRecompileGroovySource(true)

        unit.configure(configuration)
        notChanged.each { unit.addSource(it) }
        changedFiles.each { path, name -> unit.addSource(path, name) }

        classpath.each {
            unit.classLoader.addClasspath(it)
        }

        unit.compile(Phases.CLASS_GENERATION)

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
        log.info("Compilation done")
    }

    static List<String> getFileContent(String fileName, Map<String, String> changedFiles) {
        if (changedFiles.containsKey(fileName)) {
            return changedFiles.get(fileName).split('\n').toList() //TODO proper line end split
        }
        return new File(fileName).readLines()
    }

}
