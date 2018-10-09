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
import org.gls.exception.NotImplementedException

/**
 * Created by johan on 4/10/18.
 */
@Slf4j
@TypeChecked
@SuppressWarnings(["CatchException", "UnusedMethodParameter"])
class DiagnosticsParser {

    static Map<String, List<Diagnostic>> getDiagnostics(ErrorCollector errorCollector) {
        Map<String, List<Diagnostic>> diagnosticMap = [:]
        try {
            if (errorCollector == null) {
                return diagnosticMap
            }
            List<SyntaxErrorMessage> errors = errorCollector.errors
            List<Message> warnings = errorCollector.warnings
            errors?.each {
                SyntaxException exception = it.cause
                String uri = "file://" + exception.sourceLocator
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
        throw new NotImplementedException("This is not possible")
    }

    void addDiagnostic(Map<String, List<Diagnostic>> diagnostics, ExceptionMessage message) {
        String cause = message.cause
        log.info("ExceptionMessage CAUSE: $cause")
    }

    void addDiagnostic(Map<String, List<Diagnostic>> diagnostics, LocatedMessage message) {
        String cause = message.message
        log.info("LocatedMessage CAUSE: $cause")
    }

    void addDiagnostic(Map<String, List<Diagnostic>> diagnostics, SimpleMessage message) {
        String cause = message.message
        log.info("ExceptionMessage CAUSE: $cause")
    }

    void addDiagnostic(Map<String, List<Diagnostic>> diagnostics, SyntaxErrorMessage message) {
        String cause = message.cause
        log.info("SyntaxErrorMessage CAUSE: $cause")
    }

    void addDiagnostic(Map<String, List<Diagnostic>> diagnostics, WarningMessage message) {
        String cause = message.message
        log.info("WarningMessage CAUSE: $cause")
    }

    private static Diagnostic asDiagnostic(SyntaxException exception) {
        int line = exception.line - 1
        Position start = new Position(line, exception.startColumn)
        Position end = new Position(line, exception.endColumn)
        Range range = new Range(start, end)

        Diagnostic diagnostic = new Diagnostic(range, exception.message, DiagnosticSeverity.Error, "Groovy")
        return diagnostic
    }
}
