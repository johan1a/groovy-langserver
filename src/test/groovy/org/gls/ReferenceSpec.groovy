package org.gls

import static org.gls.util.TestUtil.testReference
import static org.gls.util.TestUtil.uri

import org.eclipse.lsp4j.Diagnostic
import org.eclipse.lsp4j.Location
import org.eclipse.lsp4j.Position
import org.eclipse.lsp4j.ReferenceParams
import org.eclipse.lsp4j.TextDocumentIdentifier
import org.eclipse.lsp4j.Range
import org.gls.groovy.GroovyCompilerService
import org.gls.lang.definition.ClassDefinition
import org.gls.lang.reference.ClassReference
import org.gls.lang.LanguageService
import org.gls.lang.reference.VarReference
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
@SuppressWarnings(["DuplicateStringLiteral", "DuplicateNumberLiteral"])
class ReferenceSpec extends Specification {

    void "test function return type"() {
        LanguageService finder = new LanguageService()
        String path = "src/test/test-files/3"

        GroovyCompilerService indexer = new GroovyCompilerService(uri(path), finder, new IndexerConfig())
        indexer.compile()

        Set<ClassDefinition> definitions = finder.storage.classDefinitions.findAll {
            it.type.toString() == "Box"
        }
        Set<ClassReference> usages = finder.classReferences.findAll { it.type.toString() == "Box" }

        expect:
            definitions.first().lineNumber == 0
            usages*.lineNumber.sort() == [0, 3, 4]
    }

    void "test Vardecl class usage"() {
        LanguageService finder = new LanguageService()
        String path = "src/test/test-files/4"

        GroovyCompilerService indexer = new GroovyCompilerService(uri(path), finder, new IndexerConfig())
        indexer.compile()

        Set<ClassReference> allUsages = finder.classReferences
        Set<ClassReference> usages = allUsages.findAll { it.type.toString() == "VarDeclClassUsage" }

        expect:
            usages*.lineNumber == [1, 7, 7]
    }

    void "Function reference 1"() {
        given:
            LanguageService finder = new LanguageService()
            String dirPath = "src/test/test-files/${_dir}"

            ReferenceParams params = new ReferenceParams()
            Position position = _pos
            params.position = position

            String filePath = new File(dirPath + "/${_class}.groovy").canonicalPath
            params.textDocument = new TextDocumentIdentifier(filePath)

        when:
            GroovyCompilerService indexer = new GroovyCompilerService(uri(dirPath), finder, new IndexerConfig())
            indexer.compile()
            List<Location> definitions = finder.getReferences(params)

        then:
            definitions.size() == 1

            Range range = definitions.first().range
            range.start.line == _expectedLine
            range.start.character == _expectedChar
        where:
            _dir | _pos                 | _class              | _expectedLine | _expectedChar
            9    | new Position(7, 28)  | "ClassDefinition1"  | 4             | 31
            10   | new Position(11, 17) | "FunctionReference" | 8             | 15
    }

    void "Multiple function references 1"() {
        given:
            LanguageService finder = new LanguageService()
            String dirPath = "src/test/test-files/${_dir}"

            ReferenceParams params = new ReferenceParams()
            Position position = _pos
            params.position = position

            String filePath = new File(dirPath + "/${_class}.groovy").canonicalPath
            params.textDocument = new TextDocumentIdentifier(filePath)

        when:
            GroovyCompilerService indexer = new GroovyCompilerService(uri(dirPath), finder, new IndexerConfig())
            Map<String, List<Diagnostic>> errors = indexer.compile()
            List<Location> references = finder.getReferences(params)

        then:
            errors.isEmpty()
            references.size() == _expected

        where:
            _dir              | _pos                  | _class              | _expected
            'functions/1'     | new Position(16, 23)  | "MultipleFuncRefs1" | 4
            'functions/two'   | new Position(64, 25)  | "ReferenceFinder"   | 1
            'functions/two'   | new Position(158, 49) | "ReferenceFinder"   | 3
            'functions/two'   | new Position(64, 25)  | "ReferenceFinder"   | 1
            'functions/two'   | new Position(12, 21)  | "ReferenceStorage"  | 3
            'functions/two'   | new Position(61, 23)  | "ReferenceFinder"   | 1
            'definition/1'    | new Position(1, 6)    | "Constructor"       | 2
            'small/attribute' | new Position(3, 22)   | "Attribute"         | 1
    }

    void "test VarRef in assigned value"() {
        LanguageService finder = new LanguageService()
        String path = "./src/test/test-files/small/varrefindexing"

        GroovyCompilerService indexer = new GroovyCompilerService(uri(path), finder, new IndexerConfig())
        indexer.compile()

        Set<VarReference> usages = finder.storage.varReferences
        VarReference reference = usages.find { it.varName == 'theString' }

        expect:
            reference.definitionLocation.range.start.line == 3
    }

    void "Test find references"() {
        setup:
            LanguageService finder = new LanguageService()
            String dirPath = "src/test/test-files/small/varref"

            ReferenceParams params = new ReferenceParams()
            Position position = new Position(3, 16)
            params.position = position

            String filePath = new File(dirPath + "/FindReference.groovy").canonicalPath
            params.textDocument = new TextDocumentIdentifier(filePath)

        when:
            GroovyCompilerService indexer = new GroovyCompilerService(uri(dirPath), finder, new IndexerConfig())
            indexer.compile()
            List<Location> references = finder.getReferences(params)

        then:
            references.size() == 3
            references.find { it.range.start.line == 3 } != null
            references.find { it.range.start.line == 6 } != null
            references.find { it.range.start.line == 7 } != null
    }

    void "Test find references, #_class"() {
        setup:
            LanguageService finder = new LanguageService()
            String dirPath = "src/test/test-files/small/$_dir"

            ReferenceParams params = new ReferenceParams()
            Position position = _position
            params.position = position

            String filePath = new File(dirPath + "/${_class}.groovy").canonicalPath
            params.textDocument = new TextDocumentIdentifier(filePath)

        when:
            GroovyCompilerService indexer = new GroovyCompilerService(uri(dirPath), finder, new IndexerConfig())
            indexer.compile()
            List<Location> references = finder.getReferences(params)

        then:
            references.size() == 2
            _expectedLines.each { line -> references.find { it.range.start.line == line } != null }

        where:
            _dir      | _class                   | _position           | _expectedNbr | _expectedLines
            "varref2" | "VarRefAssignment"       | new Position(3, 11) | 2            | [3, 7]
            "varref3" | "VarRefWithFunctionCall" | new Position(3, 11) | 2            | [3, 8]
    }

    void "method arguments should result in variable references"() {
        given:
            String directory = 'small/varref4/'
            String file = 'VarRefInArgument.groovy'
            List<Integer> position = [3, 29]
            List<List<Integer>> expectedResultPositions = [[3, 26]]

        expect:
            testReference(directory, file, position, expectedResultPositions)
    }

}
