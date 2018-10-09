package org.gls.lang

import org.eclipse.lsp4j.CompletionItem
import org.eclipse.lsp4j.CompletionItemKind
import org.gls.lang.definition.ClassDefinition

/**
 * Created by johan on 4/23/18.
 */
class AutoCompleter {

    List<CompletionItem> autoComplete(ClassDefinition classDefinition, String precedingText) {
        // Class.forName(classDefinition.className).declaredMethods.findAll { !it.synthetic }.name.toSet()
        Set<String> functions = classDefinition.memberFunctions
        Set<String> variables = classDefinition.memberVariables

        String prefix
        if (precedingText.endsWith(".")) {
            prefix = ""
        } else {
            prefix = precedingText.split("\\.")[1]
        }

        List<CompletionItem> result = []
        functions.each {
            if (it.startsWith(prefix)) {
                CompletionItem item = new CompletionItem(it)
                item.kind = CompletionItemKind.Method
                result.add(item)
            }
        }
        variables.each {
            if (it.startsWith(prefix)) {
                CompletionItem item = new CompletionItem(it)
                item.kind = CompletionItemKind.Variable
                result.add(item)
            }
        }

        result
    }

}

