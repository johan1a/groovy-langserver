package org.gls

import static org.gls.util.TestUtil.testReference

import spock.lang.Specification

@SuppressWarnings(["DuplicateNumberLiteral"])
class ClassReferenceSpec extends Specification {

    void "Instantiations should result in Class references"() {
        given:
            String directory = 'small/classref0/'
            String file = 'ClassRefInInstantiation.groovy'
            List<Integer> position = [1, 15]
            List<List<Integer>> expectedResultPositions = [[4, 7], [4, 39]]

        expect:
            testReference(directory, file, position, expectedResultPositions)
    }
}
