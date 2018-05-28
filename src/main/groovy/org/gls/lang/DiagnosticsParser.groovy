package org.gls.lang

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.codehaus.groovy.control.ErrorCollector
import org.codehaus.groovy.control.messages.ExceptionMessage
import org.codehaus.groovy.control.messages.LocatedMessage
import org.codehaus.groovy.control.messages.SimpleMessage
import org.codehaus.groovy.control.messages.SyntaxErrorMessage
import org.codehaus.groovy.control.messages.WarningMessage
import org.codehaus.groovy.syntax.SyntaxException
import org.eclipse.lsp4j.Diagnostic
import org.eclipse.lsp4j.DiagnosticSeverity
import org.eclipse.lsp4j.Position
import org.eclipse.lsp4j.jsonrpc.messages.Message
import org.eclipse.lsp4j.Range

/**
 * Created by johan on 4/10/18.
 */
@Slf4j
@TypeChecked
class DiagnosticsParser {

    public static Map<String, List<Diagnostic>> getDiagnostics(ErrorCollector errorCollector) {
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
                addDiagnostic(diagnosticMap, it)
                log.debug it.toString()
                log.debug "TODO implement warning diagnostics"
            }
        } catch (Exception e) {
            log.error("Error", e)
        }
        return diagnosticMap
    }


    void addDiagnostic(Map<String, List<Diagnostic>> diagnostics, Message message) {
        throw new Exception("This is not possible")

    }

    void addDiagnostic(Map<String, List<Diagnostic>> diagnostics, ExceptionMessage message) {
        String cause = message.getCause()
        log.info("ExceptionMessage CAUSE: $cause")
    }

    void addDiagnostic(Map<String, List<Diagnostic>> diagnostics, LocatedMessage message) {
        String cause = message.getMessage()
        log.info("LocatedMessage CAUSE: $cause")

    }

    void addDiagnostic(Map<String, List<Diagnostic>> diagnostics, SimpleMessage message) {
        String cause = message.message
        log.info("ExceptionMessage CAUSE: $cause")

    }

    void addDiagnostic(Map<String, List<Diagnostic>> diagnostics, SyntaxErrorMessage message) {
        String cause = message.getCause()
        log.info("SyntaxErrorMessage CAUSE: $cause")

    }

    void addDiagnostic(Map<String, List<Diagnostic>> diagnostics, WarningMessage message) {
        String cause = message.message
        log.info("WarningMessage CAUSE: $cause")
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
