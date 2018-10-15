package org.gls

import static org.gls.util.TestUtil.testReference

import spock.lang.Specification

@SuppressWarnings(["DuplicateNumberLiteral", "DuplicateListLiteral"])
class FuncRefSpec extends Specification {

    void "Method should result in func references"() {
        given:
            String directory = 'small/funcref/basicfuncref'
            String file = 'Flabbergast.groovy'
            List<Integer> position = [4, 9]
            List<List<Object>> expectedResultPositions = [[6, 20, "BasicFuncRef.groovy"]]

        expect:
            testReference(directory, file, position, expectedResultPositions)
    }

    void "Static method should result in func references"() {
        given:
            String directory = 'small/funcref/staticfuncref'
            String file = 'Flabbergast.groovy'
            List<Integer> position = [4, 16]
            List<List<Object>> expectedResultPositions = [[5, 20, "StaticFuncRef.groovy"]]

        expect:
            testReference(directory, file, position, expectedResultPositions)
    }
}
