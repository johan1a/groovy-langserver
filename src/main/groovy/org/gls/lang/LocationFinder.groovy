package org.gls.lang

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotatedNode
import org.codehaus.groovy.ast.Variable
import org.eclipse.lsp4j.Position
import org.eclipse.lsp4j.Range

@TypeChecked
@Slf4j
class LocationFinder {

    static ImmutableLocation findLocation(String sourceFilePath, List<String> sourceLines, AnnotatedNode node,
                                          String name) {
        return findLocation(sourceFilePath, sourceLines, node, name, node.annotations.size())
    }

    // Returns the line number of the first { after the class declaration
    static int findClassStart(List<String> sourceLines, String name) {
        int classNameLine = findFirstLineContaining(sourceLines, name)
        int classBracketLine = findFirstLineContaining(sourceLines.drop(classNameLine), name)
        return classNameLine + classBracketLine
    }

    private static int findFirstLineContaining(List<String> sourceLines, String name) {
        String line = sourceLines.find {
            it.contains(name)
        }
        int rowNbr = sourceLines.indexOf(line)
        rowNbr
    }

    static ImmutableLocation findLocation(String sourceFilePath, List<String> sourceLines, ASTNode node,
                                          String fullName, int lineOffset = 0) {
        int lineNumber = node.lineNumber - 1 + lineOffset
        int lastLineNumber = lineNumber
        int columnNumber
        int lastColumnNumber
        if (lineNumber >= 0) {
            String firstLine = sourceLines[lineNumber]
            String shortName = fullName.split("\\.").last()
            if (firstLine != null && shortName != null && firstLine.contains(shortName)) {
                columnNumber = firstLine.indexOf(shortName, node.columnNumber - 1)
                lastColumnNumber = columnNumber + shortName.size() - 1
            } else {
                log.trace("line doesn't contain name: ${fullName}, line: $firstLine")
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
