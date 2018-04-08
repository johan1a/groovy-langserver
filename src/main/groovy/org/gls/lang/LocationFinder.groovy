package org.gls.lang

import groovy.transform.TypeChecked
import org.codehaus.groovy.ast.ASTNode
import org.eclipse.lsp4j.Location
import org.eclipse.lsp4j.Position
import org.eclipse.lsp4j.Range

/**
 * Created by johan on 4/8/18.
 */
@TypeChecked
class LocationFinder {

    static Location findLocation(String sourceFilePath, List<String> source, ASTNode node, String name) {
        int lineNumber = node.lineNumber - 1
        int lastLineNumber = lineNumber
        int columnNumber
        int lastColumnNumber
        if (lineNumber > 0) {
            String firstLine = source[lineNumber]
            columnNumber = firstLine.indexOf(name, node.columnNumber - 1)
            lastColumnNumber = columnNumber + name.size() - 1
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
