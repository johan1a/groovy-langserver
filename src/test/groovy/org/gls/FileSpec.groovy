package org.gls

import org.eclipse.lsp4j.*
import org.gls.groovy.GroovyIndexer
import org.gls.lang.ReferenceFinder
import spock.lang.Specification
import spock.lang.Unroll

import static org.gls.util.TestUtil.uriList

@Unroll
class FileSpec extends Specification {

    def "Rename in file"() {
        given:
        ReferenceFinder finder = new ReferenceFinder()
        String dirPath = "src/test/test-files/functions/two"
        RenameParams params = new RenameParams()
        params.position = _pos
        params.newName = _newText
        String filePath = new File(dirPath + "/ReferenceFinder.groovy").getCanonicalPath()
        params.setTextDocument(new TextDocumentIdentifier(filePath))

        when:
        GroovyIndexer indexer = new GroovyIndexer(uriList(dirPath), finder)
        Map<String, List<Diagnostic>> errors = indexer.index()
        List<TextEdit> edits = finder.rename(params).values().flatten()

        then:
        errors.isEmpty()
        edits.every { it.newText == _newText }
        edits.size() == _size
        edits.collect {
            [
                    line : it.range.start.line,
                    start: it.range.start.character,
                    end  : it.range.end.character
            ]
        }

        where:
        _pos                  | _newText     | _class            | _size | _edits
        new Position(122, 78) | "coolerName" | "ReferenceFinder" | 3     | [[line: 119, start: 73, end: 78],
                                                                            [line: 120, start: 73, end: 78],
                                                                            [line: 122, start: 77, end: 83]]
        new Position(119, 73) | "coolerName" | "ReferenceFinder" | 3     | [[line: 119, start: 73, end: 78],
                                                                            [line: 120, start: 73, end: 78],
                                                                            [line: 122, start: 77, end: 83]]

    }
}
