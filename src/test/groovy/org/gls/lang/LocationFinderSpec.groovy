package org.gls.lang

import org.codehaus.groovy.ast.ASTNode
import spock.lang.Specification

class LocationFinderSpec extends Specification {

    void "Test findLocation"() {
        given:
            String sourceFilePath = "x"
            List<String> source = ["def varName = new ClassName()"]
            String fullName = "pkg.test.ClassName"
            ASTNode node = new ASTNode(lineNumber: 1)

        when:
            ImmutableLocation location = LocationFinder.findLocation(sourceFilePath, source, node, fullName)

        then:
            location.range.start.line == 0
            location.range.end.line == 0
            location.range.start.character == 18
            location.range.end.character == 26
    }
}
