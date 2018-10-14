package org.gls

import static org.gls.util.TestUtil.testReference

import spock.lang.Specification

@SuppressWarnings(["DuplicateNumberLiteral", "DuplicateListLiteral"])
class VarReferenceSpec extends Specification {

    void "Closure argument should result in Var reference"() {
        given:
            String directory = 'small/vardecl0/'
            String file = 'VarRefInClosureArgumentWithoutType.groovy'
            List<Integer> position = [4, 36]
            List<List<Integer>> expectedResultPositions = [[4, 36], [9, 19]]

        expect:
            testReference(directory, file, position, expectedResultPositions)
    }
}
