package org.gls

import static org.gls.util.TestUtil.testReference

import spock.lang.Specification

@SuppressWarnings(["DuplicateNumberLiteral", "DuplicateListLiteral"])
class VarReferenceSpec extends Specification {

    void "Closure argument should result in Var reference"() {
        given:
            String directory = 'small/varref5/'
            String file = 'VarRefInClosureArgument.groovy'
            List<Integer> position = [7, 19]
            List<List<Integer>> expectedResultPositions = [[6, 42], [7, 19]]

        expect:
            testReference(directory, file, position, expectedResultPositions)
    }
}
