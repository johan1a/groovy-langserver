package org.gls

import static org.gls.util.TestUtil.testDeclaration

import spock.lang.Specification

@SuppressWarnings(["DuplicateNumberLiteral", "DuplicateListLiteral"])
class VarDeclSpec extends Specification {

    void "Closure argument should result in Var declaration"() {
        given:
            String directory = 'small/vardecl0/'
            String file = 'VarRefInClosureArgumentWithoutType.groovy'
            List<Integer> position = [4, 40]
            List<Integer> expectedResultPositions = [4, 36]

        expect:
            testDeclaration(directory, file, position, expectedResultPositions)
    }

}
