package org.gls.groovy

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.codehaus.groovy.ast.ModuleNode
import org.codehaus.groovy.control.CompilationUnit
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.MultipleCompilationErrorsException
import org.codehaus.groovy.control.Phases
import org.eclipse.lsp4j.Diagnostic
import org.gls.ConfigService
import org.gls.IndexerConfig
import org.gls.UriUtils
import org.gls.lang.ClassPreprocessor
import org.gls.lang.DiagnosticsParser
import org.gls.lang.LanguageService

@Slf4j
@CompileStatic
class GroovyCompilerService {

    private static final int MILLIS_PER_SECOND = 1000
    private static final int CONFIGURATION_TOLERANCE = 1000_000

    List<URI> sourcePaths

    LanguageService service = new LanguageService()
    URI rootUri
    ConfigService configService = new ConfigService()
    String buildConfigLocation = "build.gradle"
    IndexerConfig indexerConfig

    GroovyCompilerService(URI rootUri, LanguageService service, IndexerConfig indexerConfig) {
        this.rootUri = rootUri
        this.indexerConfig = indexerConfig
        sourcePaths = [
                UriUtils.appendURI(rootUri, "/src/main/groovy"),
                UriUtils.appendURI(rootUri, "/grails-app/domain"),
                UriUtils.appendURI(rootUri, "/grails-app/services"),
        ]
        if (indexerConfig.scanAllSubDirs) {
            sourcePaths = [rootUri]
        }
        log.info "sourcePaths: ${sourcePaths}"

        this.service = service
    }

    Map<String, List<Diagnostic>> compile(Map<String, String> changedFiles = [:]) {
        List<File> files = new LinkedList<>()
        findFilesRecursive(files, changedFiles)
        return index(files, changedFiles)
    }

    void findFilesRecursive(List<File> files, Map<String, String> modifiedFiles) {
        sourcePaths.each {
            try {
                File basedir = new File(it)
                basedir.eachFileRecurse {
                    String filename = it.name
                    if (shouldAddLogField(it.path, filename)) {
                        addLogField(modifiedFiles, filename, it)
                    } else if (filename =~ /.*\.groovy/) {
                        files.add(it)
                    }
                }
            } catch (FileNotFoundException e) {
                log.debug("Source dir not found", e)
            }
        }
    }

    private void addLogField(Map<String, String> modifiedFiles, String filename, File file) {
        if (modifiedFiles.containsKey(filename)) {
            modifiedFiles[file.path] = ClassPreprocessor.addLogField(filename, modifiedFiles[filename])
        } else {
            String content = ClassPreprocessor.addLogField(filename, file.text)
            modifiedFiles[file.path] = content
        }
    }

    private boolean shouldAddLogField(String path, String filename) {
        path.contains("grails-app/") &&
                (filename =~ /.*Service\.groovy/ || filename =~ /.*Controller\.groovy/)
    }

    Map<String, List<Diagnostic>> index(List<File> files, Map<String, String> changedFiles) {
        Map<String, List<Diagnostic>> diagnostics = [:]
        try {
            log.info("Starting indexing")
            long start = System.currentTimeMillis()
            List<String> classpath = dependencies
            doCompile(files, changedFiles, classpath)
            long elapsed = System.currentTimeMillis() - start
            log.info("Indexing done in ${elapsed / MILLIS_PER_SECOND}s")
        } catch (MultipleCompilationErrorsException e) {
            log.debug("Got MultipleCompilationErrorsException")
            diagnostics = DiagnosticsParser.getDiagnostics(e.errorCollector)
            if (diagnostics.isEmpty()) {
                log.error("Compilation error without diagnostics:", e)
            }
        } catch (NoClassDefFoundError e) {
            log.error("Compilation error:", e)
        }
        service.correlate()
        log.info("diagnostics size: ${diagnostics.size()}")
        diagnostics.each { log.debug(it.toString()) }
        return diagnostics
    }

    private List<String> getDependencies() {
        if (indexerConfig.scanDependencies) {
            configService.getDependencies(rootUri, buildConfigLocation)
        } else {
            return []
        }
    }

    private void doCompile(List<File> files, Map<String, String> changedFiles, List<String> classpath) {
        log.info("compiling...")
        List<File> notChanged = files.findAll { !changedFiles.keySet().contains(it.canonicalPath) }

        CompilerConfiguration configuration = new CompilerConfiguration()
        configuration.tolerance = CONFIGURATION_TOLERANCE
        configuration.recompileGroovySource = true

        CompilationUnit unit = new CompilationUnit(configuration)

        notChanged.each { unit.addSource(it) }
        changedFiles.each { path, name -> unit.addSource(path, name) }

        classpath.each {
            unit.classLoader.addClasspath(it)
        }

        unit.compile(Phases.CLASS_GENERATION)

        unit.iterator().each { sourceUnit ->
            log.debug("compiling ${sourceUnit.name}")
            ModuleNode moduleNode = sourceUnit.AST

            String name = sourceUnit.name
            List<String> fileContents = getFileContent(name, changedFiles)
            CodeVisitor codeVisitor = new CodeVisitor(service, name, fileContents)
            moduleNode.visit(codeVisitor)
            moduleNode.classes.each { classNode ->
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
