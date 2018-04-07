package org.gls

import org.eclipse.lsp4j.Location
import org.eclipse.lsp4j.Position
import org.eclipse.lsp4j.TextDocumentIdentifier
import org.eclipse.lsp4j.TextDocumentPositionParams
import org.gls.groovy.GroovyIndexer
import org.gls.lang.ClassDefinition
import org.gls.lang.ClassUsage
import org.gls.lang.ReferenceFinder
import spock.lang.Specification

import static org.gls.util.TestUtil.uriList

class ClassSpec extends Specification {

    def "test function return type"() {
        ReferenceFinder finder = new ReferenceFinder()
        String path = "src/test/test-files/3"

        GroovyIndexer indexer = new GroovyIndexer(uriList(path), finder)
        indexer.index()

        String testFilePath = new File(path + "/FunctionReturnType.groovy").getCanonicalPath()

        ClassDefinition definition = finder.storage.getClassDefinitions().find{it.getFullClassName() == "Box"}
        def usages = finder.getClassUsages(testFilePath)
        ClassUsage usage = usages.first()

        expect:
        definition.lineNumber == 0
        usage.lineNumber == 3
    }

    def "test Vardecl class usage"() {
        ReferenceFinder finder = new ReferenceFinder()
        String path = "src/test/test-files/4"

        GroovyIndexer indexer = new GroovyIndexer(uriList(path), finder)
        indexer.index()

        String testFilePath = new File(path + "/VarDeclClassUsage.groovy").getCanonicalPath()
        Set<ClassUsage> usages = finder.getClassUsages(testFilePath)
        ClassUsage usage = usages.find { it.referencedClassName == "VarDeclClassUsage" }

        expect:
        usage.lineNumber == 7
    }

    def "Class definition"() {
        given:
        ReferenceFinder finder = new ReferenceFinder()
        String dirPath = "src/test/test-files/9"

        TextDocumentPositionParams params = new TextDocumentPositionParams()
        Position position = new Position(4, 21)
        params.position = position

        String filePath = new File(dirPath + "/ClassDefinition1.groovy").getCanonicalPath()
        params.setTextDocument(new TextDocumentIdentifier(filePath))

        when:
        GroovyIndexer indexer = new GroovyIndexer(uriList(dirPath), finder)
        indexer.index()
        List<Location> definitions = finder.getDefinition(params)
        Set<ClassUsage> usages = finder.storage.getClassUsages()

        then:
        definitions.size() == 1
        definitions.first().range.start.line == 1
        usages.size() == 3
        ClassUsage usage = usages.find{ it.lineNumber == 4}
        usage.columnNumber == 8
        usage.lastColumnNumber == 23
    }

}
