package org.gls

import static org.gls.util.TestUtil.uri

import org.eclipse.lsp4j.Diagnostic
import org.eclipse.lsp4j.Location
import org.eclipse.lsp4j.Position
import org.eclipse.lsp4j.ReferenceParams
import org.eclipse.lsp4j.TextDocumentIdentifier
import org.eclipse.lsp4j.TextDocumentPositionParams
import org.eclipse.lsp4j.Range
import org.gls.groovy.GroovyCompilerService
import org.gls.lang.reference.ClassReference
import org.gls.lang.ImmutablePosition
import org.gls.lang.LanguageService
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
@SuppressWarnings(["DuplicateNumberLiteral", "DuplicateStringLiteral", "DuplicateListLiteral"])
class DefinitionSpec extends Specification {

    void "Function definition"() {
        given:
            LanguageService finder = new LanguageService()
            String dirPath = "src/test/test-files/8"

            TextDocumentPositionParams params = new TextDocumentPositionParams()
            ImmutablePosition position = new ImmutablePosition(4, 13)
            params.position = position

            String filePath = new File(dirPath + "/FunctionDefinition.groovy").canonicalPath
            params.textDocument = new TextDocumentIdentifier(filePath)

        when:
            GroovyCompilerService compilerService = new GroovyCompilerService(uri(dirPath), finder, new IndexerConfig())
            compilerService.sourcePaths = [compilerService.rootUri]
            Map<String, List<Diagnostic>> errors = compilerService.compile()
            List<Location> definitions = finder.getDefinition(params)

        then:
            errors.isEmpty()
            definitions.size() == 1
            definitions.first().range.start.line == 3
    }

    void "Class definition"() {
        given:
            LanguageService finder = new LanguageService()
            String dirPath = "src/test/test-files/9"

            TextDocumentPositionParams params = new TextDocumentPositionParams()
            ImmutablePosition position = new ImmutablePosition(4, 21)
            params.position = position

            String filePath = new File(dirPath + "/ClassDefinition1.groovy").canonicalPath
            params.textDocument = new TextDocumentIdentifier(filePath)

        when:
            GroovyCompilerService indexer = new GroovyCompilerService(uri(dirPath), finder, new IndexerConfig())
            indexer.compile()
            List<Location> definitions = finder.getDefinition(params)
            Set<ClassReference> usages = finder.classReferences

        then:
            definitions.size() == 1
            definitions.first().range.start.line == 1
            usages.size() == 18
            ClassReference usage = usages.find { it.lineNumber == 4 }
            usage.columnNumber == 8
            usage.lastColumnNumber == 23
    }

    void "Find definition"() {
        given:
            LanguageService finder = new LanguageService()
            String dirPath = "src/test/test-files/${_dir}"

            TextDocumentPositionParams params = new TextDocumentPositionParams()
            ImmutablePosition position = new ImmutablePosition(_pos[0], _pos[1])
            params.position = position

            String filePath = new File(dirPath + "/${_class}.groovy").canonicalPath
            params.textDocument = new TextDocumentIdentifier(filePath)

        when:
            GroovyCompilerService indexer = new GroovyCompilerService(uri(dirPath), finder, new IndexerConfig())
            Map<String, List<Diagnostic>> errors = indexer.compile()
            List<Location> definitions = finder.getDefinition(params)

        then:
            errors.isEmpty()
            definitions.size() == 1
            definitions.first().uri.startsWith("/")
            Range range = definitions.first().range
            range.start == new ImmutablePosition(_expected[0], _expected[1])
            range.end.character == _end

        where:
            _dir                           | _pos     | _class             | _expected | _end
            "9"                            | [4, 36]  | "ClassDefinition1" | [7, 21]   | 31
            'functions/two'                | [72, 46] | "ReferenceFinder"  | [142, 25] | 45
            'functions/two'                | [12, 8]  | "ReferenceFinder"  | [12, 6]   | 21
            'functions/two'                | [19, 8]  | "ReferenceFinder"  | [12, 21]  | 27
            'functions/two'                | [71, 47] | "ReferenceFinder"  | [12, 21]  | 27
            'definition/1'                 | [3, 14]  | "Constructor"      | [1, 6]    | 16
            'definition/language_service/' | [10, 23] | "LanguageService"  | [25, 35]  | 44
    }

    void "Repeated query"() {
        given:
            String dirPath = "src/test/test-files/functions/two"

            TextDocumentPositionParams params1 = new TextDocumentPositionParams()
            ImmutablePosition position1 = new ImmutablePosition(72, 46)
            params1.position = position1

            String filePath = new File(dirPath + "/ReferenceFinder.groovy").canonicalPath
            params1.textDocument = new TextDocumentIdentifier(filePath)

            IndexerConfig indexerConfig = new IndexerConfig(scanAllSubDirs: true, serializeLanguageService: false)
        when:
            GroovyTextDocumentService service = new GroovyTextDocumentService(indexerConfig)
            service.rootUri = uri(dirPath)
            service.compile()
            LanguageService finder = service.languageService
            List<Location> definitions1 = finder.getDefinition(params1)

        then:
            definitions1.size() == 1
            definitions1.first().uri.startsWith("/")
            Range range1 = definitions1.first().range
            range1.start == new ImmutablePosition(142, 25)
            range1.end.character == 45

        when:
            ReferenceParams params2 = new ReferenceParams()
            params2.textDocument = new TextDocumentIdentifier(filePath)
            ImmutablePosition position2 = new ImmutablePosition(12, 25)
            params2.position = position2
            List<Location> definitions2 = finder.getReferences(params2)

        then:
            finder.storage.varReferences.every { it.location.uri.startsWith("/") }
            finder.storage.varDefinitions.every { it.location.uri.startsWith("/") }
            definitions2.size() == 18
            definitions2.first().uri.startsWith("/")
            Range range2 = definitions2.first().range
            range2.start == new ImmutablePosition(12, 21)
            range2.end.character == 27
    }

    void "Test method argument"() {
        given:
            LanguageService finder = new LanguageService()
            String dirPath = "src/test/test-files/7"

            TextDocumentPositionParams params = new TextDocumentPositionParams()
            ImmutablePosition position = new ImmutablePosition(12, 18)
            params.position = position

            String filePath = new File(dirPath + "/MethodArgument.groovy").canonicalPath
            params.textDocument = new TextDocumentIdentifier(filePath)

        when:
            GroovyCompilerService indexer = new GroovyCompilerService(uri(dirPath), finder, new IndexerConfig())
            Map<String, List<Diagnostic>> errors = indexer.compile()
            List<Location> definitions = finder.getDefinition(params)

        then:
            errors.isEmpty()
            definitions.size() == 1
            definitions.first().range.start.line == 11
    }

    void "Test grails generated log field"() {
        setup:
            LanguageService finder = new LanguageService()
            String dirPath = "src/test/test-files/grails"

            ReferenceParams params = new ReferenceParams()
            Position position = _position
            params.position = position

            String filePath = new File(dirPath + "/grails-app/services/${_class}.groovy").canonicalPath
            params.textDocument = new TextDocumentIdentifier(filePath)

        when:
            GroovyCompilerService indexer = new GroovyCompilerService(uri(dirPath), finder, new IndexerConfig())
            indexer.compile()

            Map<String, List<Diagnostic>> errors = indexer.compile()
            List<Location> definitions = finder.getDefinition(params)

        then:
            errors.isEmpty()
            definitions.size() == 1
            definitions.first().range.start.line == 1

        where:
            _class        | _position          | _expectedNbr | _expectedLine
            "TestService" | new Position(4, 8) | 1            | 7
    }

}
