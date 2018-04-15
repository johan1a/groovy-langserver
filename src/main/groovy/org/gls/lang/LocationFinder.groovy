package org.gls.lang

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotatedNode
import org.eclipse.lsp4j.Position
import org.eclipse.lsp4j.Range

@TypeChecked
@Slf4j
class LocationFinder {

    static ImmutableLocation findLocation(String sourceFilePath, List<String> source, AnnotatedNode node, String name) {
        return findLocation(sourceFilePath, source, node, name, node.getAnnotations().size())
    }

    static ImmutableLocation findLocation(String sourceFilePath, List<String> source, ASTNode node, String name, int lineOffset = 0) {
        int lineNumber = node.lineNumber - 1 + lineOffset
        int lastLineNumber = lineNumber
        int columnNumber
        int lastColumnNumber
        if (lineNumber > 0) {
            String firstLine = source[lineNumber]
            if(firstLine != null && name != null && firstLine.contains(name)){
                columnNumber = firstLine.indexOf(name, node.columnNumber - 1)
                lastColumnNumber = columnNumber + name.size() - 1
            } else {
                log.warn("line doesn't contain name: $name, line: $firstLine")
                columnNumber = node.columnNumber
                lastColumnNumber = node.lastColumnNumber
            }
        } else {
            columnNumber = node.columnNumber
            lastColumnNumber = node.lastColumnNumber
        }
        Position start = new ImmutablePosition(lineNumber, columnNumber)
        Position end = new ImmutablePosition(lastLineNumber, lastColumnNumber)

        Range range = new ImmutableRange(start, end)
        return new ImmutableLocation(sourceFilePath, range)
    }
}
