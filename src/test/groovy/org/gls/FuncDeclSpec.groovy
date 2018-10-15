package org.gls

import static org.gls.util.TestUtil.testDeclaration

import spock.lang.Specification

class FuncDeclSpec extends Specification {

    void "Method should result in func references"() {
        given:
            String directory = 'small/funcref/basicfuncref'
            String file = 'BasicFuncRef.groovy'
            List<Integer> position = [6, 20]
            List<Object> expectedResultPositions = [4, 9, "Flabbergast.groovy"]

        expect:
            testDeclaration(directory, file, position, expectedResultPositions)
    }

}
