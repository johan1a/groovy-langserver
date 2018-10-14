package org.gls

import static org.gls.util.TestUtil.testDeclaration

import spock.lang.Specification

@SuppressWarnings(["DuplicateNumberLiteral", "DuplicateListLiteral"])
class VarDeclSpec extends Specification {

    void "Closure argument should result in Var declaration"() {
        given:
            String directory = 'small/varref5/'
            String file = 'VarRefInClosureArgument.groovy'
            List<Integer> position = [6, 42]
            List<Integer> expectedResultPositions = [6, 42]

        expect:
            testDeclaration(directory, file, position, expectedResultPositions)
    }

}
