package org.gls.lang

import org.codehaus.groovy.ast.ClassNode
import org.eclipse.lsp4j.CompletionItem
import org.eclipse.lsp4j.CompletionItemKind
import org.gls.lang.definition.ClassDefinition
import org.gls.lang.definition.FuncDefinition
import spock.lang.Specification
import spock.lang.Unroll

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

}
