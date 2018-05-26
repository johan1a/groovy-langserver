package org.gls.lang

import org.eclipse.lsp4j.CompletionItem
import org.eclipse.lsp4j.CompletionItemKind
import org.eclipse.lsp4j.Diagnostic
import org.eclipse.lsp4j.Position
import org.eclipse.lsp4j.ReferenceParams
import org.eclipse.lsp4j.TextDocumentIdentifier
import org.gls.CompletionRequest
import org.gls.IndexerConfig
import org.gls.groovy.GroovyCompilerService
import org.gls.lang.definition.ClassDefinition
import spock.lang.Specification
import spock.lang.Unroll

import static org.gls.util.TestUtil.uri

@Unroll
class AutoCompleterSpec extends Specification {

    AutoCompleter autoCompleter = new AutoCompleter()

    def "AutoComplete"() {
        given:
        ClassDefinition classDefinition = new ClassDefinition()

        classDefinition.memberFunctions.add(_funcName)
        classDefinition.memberFunctions.add("bFunc")

        List<CompletionItem> complete = autoCompleter.autoComplete(classDefinition, _precedingText)

        expect:
        complete.size() == _size
        if (_size > 0) {
            complete[0]?.label == _funcName
            complete[0]?.kind == CompletionItemKind.Method
        }

        where:
        _funcName | _precedingText | _size
        "aFunc"   | "name.a"       | 1
        "aFunc"   | "e.aFun"       | 1
        "aFunc"   | "e.c"          | 0
        "aFunc"   | "e."           | 2
    }

    def "Autocomplete 2"() {
        given:
        LanguageService finder = new LanguageService()
        String dirPath = "src/test/test-files/complete/${_dir}"

        ReferenceParams params = new ReferenceParams()
        Position position = _pos
        params.position = position


        String path = dirPath + "/${_class}.groovy"
        String filePath = new File(path).getCanonicalPath()
        params.setTextDocument(new TextDocumentIdentifier(filePath))

        when:
        GroovyCompilerService indexer = new GroovyCompilerService(uri(dirPath), finder, new IndexerConfig())
        Map<String, List<Diagnostic>> errors = indexer.compile()

        CompletionRequest request = new CompletionRequest(position: _pos, precedingText: _preceding, uri: new File(path).getCanonicalPath())
        List<CompletionItem> references = finder.getCompletionItems(request)

        then:
        errors.isEmpty()
        references*.label.containsAll(_expected)

        where:
        _dir | _pos                | _preceding | _class     | _expected
        '1'  | new Position(5, 10) | "comp."    | "Complete" | ["hey", "coolFunc"]
    }

}
