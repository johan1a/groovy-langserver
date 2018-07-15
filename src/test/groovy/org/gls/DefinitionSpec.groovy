package org.gls

import org.eclipse.lsp4j.*
import org.gls.groovy.GroovyCompilerService
import org.gls.lang.reference.ClassReference
import org.gls.lang.ImmutablePosition
import org.gls.lang.LanguageService
import org.gls.util.TestUtil
import spock.lang.Specification
import spock.lang.Unroll

import static org.gls.util.TestUtil.uri

@Unroll
class DefinitionSpec extends Specification {


    def "Function definition"() {
        given:
        LanguageService finder = new LanguageService()
        String dirPath = "src/test/test-files/8"

        TextDocumentPositionParams params = new TextDocumentPositionParams()
        ImmutablePosition position = new ImmutablePosition(4, 13)
        params.position = position

        String filePath = new File(dirPath + "/FunctionDefinition.groovy").getCanonicalPath()
        params.setTextDocument(new TextDocumentIdentifier(filePath))

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

    def "Class definition"() {
        given:
        LanguageService finder = new LanguageService()
        String dirPath = "src/test/test-files/9"

        TextDocumentPositionParams params = new TextDocumentPositionParams()
        ImmutablePosition position = new ImmutablePosition(4, 21)
        params.position = position

        String filePath = new File(dirPath + "/ClassDefinition1.groovy").getCanonicalPath()
        params.setTextDocument(new TextDocumentIdentifier(filePath))

        when:
        GroovyCompilerService indexer = new GroovyCompilerService(uri(dirPath), finder, new IndexerConfig())
        indexer.compile()
        List<Location> definitions = finder.getDefinition(params)
        Set<ClassReference> usages = finder.getClassReferences()

        then:
        definitions.size() == 1
        definitions.first().range.start.line == 1
        usages.size() == 18
        ClassReference usage = usages.find { it.lineNumber == 4 }
        usage.columnNumber == 8
        usage.lastColumnNumber == 23
    }

    def "Find definition"() {
        given:
        LanguageService finder = new LanguageService()
        String dirPath = "src/test/test-files/${_dir}"

        TextDocumentPositionParams params = new TextDocumentPositionParams()
        ImmutablePosition position = _pos
        params.position = position

        String filePath = new File(dirPath + "/${_class}.groovy").getCanonicalPath()
        params.setTextDocument(new TextDocumentIdentifier(filePath))

        when:
        GroovyCompilerService indexer = new GroovyCompilerService(uri(dirPath), finder, new IndexerConfig())
        def errors = indexer.compile()
        List<Location> definitions = finder.getDefinition(params)

        then:
        errors.isEmpty()
        definitions.size() == 1
        definitions.first().uri.startsWith("/")
        Range range = definitions.first().range
        range.start == _expected
        range.end.character == _end

        where:
        _dir            | _pos                          | _class             | _expected                      | _end
        "9"             | new ImmutablePosition(4, 36)  | "ClassDefinition1" | new ImmutablePosition(7, 21)   | 31
        'functions/two' | new ImmutablePosition(72, 46) | "ReferenceFinder"  | new ImmutablePosition(142, 25) | 45
        'functions/two' | new ImmutablePosition(12, 8)  | "ReferenceFinder"  | new ImmutablePosition(12, 6)   | 21
        'functions/two' | new ImmutablePosition(19, 8)  | "ReferenceFinder"  | new ImmutablePosition(12, 21)  | 27
        'functions/two' | new ImmutablePosition(71, 47) | "ReferenceFinder"  | new ImmutablePosition(12, 21)  | 27
        'definition/1'  | new ImmutablePosition(3, 14)  | "Constructor"      | new ImmutablePosition(1, 6)    | 16
    }

    def "Repeated query"() {
        given:
        String dirPath = "src/test/test-files/functions/two"

        TextDocumentPositionParams params1 = new TextDocumentPositionParams()
        ImmutablePosition position1 = new ImmutablePosition(72, 46)
        params1.position = position1

        String filePath = new File(dirPath + "/ReferenceFinder.groovy").getCanonicalPath()
        params1.setTextDocument(new TextDocumentIdentifier(filePath))

        when:
        GroovyTextDocumentService service = new GroovyTextDocumentService(new IndexerConfig(scanAllSubDirs: true))
        service.setRootUri(uri(dirPath))
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
        params2.setTextDocument(new TextDocumentIdentifier(filePath))
        ImmutablePosition position2 = new ImmutablePosition(12, 25)
        params2.position = position2
        List<Location> definitions2 = finder.getReferences(params2)

        then:
        finder.storage.varReferences.every { it.location.uri.startsWith("/") }
        finder.storage.varDefinitions.every { it.location.uri.startsWith("/") }
        definitions2.size() == 17
        definitions2.first().uri.startsWith("/")
        Range range2 = definitions2.first().range
        range2.start == new ImmutablePosition(15, 15)
        range2.end.character == 21


    }

    def "Test method argument"() {
        given:
        LanguageService finder = new LanguageService()
        String dirPath = "src/test/test-files/7"

        TextDocumentPositionParams params = new TextDocumentPositionParams()
        ImmutablePosition position = new ImmutablePosition(12, 18)
        params.position = position

        String filePath = new File(dirPath + "/MethodArgument.groovy").getCanonicalPath()
        params.setTextDocument(new TextDocumentIdentifier(filePath))

        when:
        GroovyCompilerService indexer = new GroovyCompilerService(uri(dirPath), finder, new IndexerConfig())
        Map<String, List<Diagnostic>> errors = indexer.compile()
        List<Location> definitions = finder.getDefinition(params)


        then:
        errors.isEmpty()
        definitions.size() == 1
        definitions.first().range.start.line == 11
    }

    def "Test grails generated log field"() {
        setup:
            LanguageService finder = new LanguageService()
            String dirPath = "src/test/test-files/grails"

            ReferenceParams params = new ReferenceParams()
            Position position = _position
            params.position = position

            String filePath = new File(dirPath + "/grails-app/services/${_class}.groovy").getCanonicalPath()
            params.setTextDocument(new TextDocumentIdentifier(filePath))

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
            _class        | _position           | _expectedNbr | _expectedLine
            "TestService" | new Position(4, 8) | 1            | 7
    }

}
