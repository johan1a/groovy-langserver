package org.gls.lang

import org.eclipse.lsp4j.CompletionItem
import org.eclipse.lsp4j.CompletionItemKind
import org.gls.lang.definition.ClassDefinition
import org.gls.lang.definition.FuncDefinition
import org.gls.lang.definition.VarDefinition

/**
 * Created by johan on 4/23/18.
 */
class AutoCompleter {

    List<CompletionItem> autoComplete(ClassDefinition classDefinition, String precedingText) {
        Set<FuncDefinition> functions = classDefinition.memberFunctions
        Set<VarDefinition> variables = classDefinition.memberVariables

        String prefix
        if (precedingText.endsWith(".")) {
            prefix = ""
        } else {
            prefix = precedingText.split("\\.")[1]
        }

        List<CompletionItem> result = []
        functions.each { FuncDefinition it ->
            if (it.functionName.startsWith(prefix)) {
                CompletionItem item = new CompletionItem(it.functionName)
                item.setKind(CompletionItemKind.Method)
                result.add(item)
            }
        }
        variables.each { VarDefinition it ->
            if (it.varName.startsWith(prefix)) {
                CompletionItem item = new CompletionItem(it.functionName)
                item.setKind(CompletionItemKind.Variable)
                result.add(item)
            }
        }

        result
    }

}

