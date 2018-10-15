package org.gls

import static org.gls.util.TestUtil.testDeclaration

import spock.lang.Specification

class FuncDeclSpec extends Specification {

    void "Method should result in func declaration"() {
        given:
            String directory = 'small/funcref/basicfuncref'
            String file = 'BasicFuncRef.groovy'
            List<Integer> position = [6, 20]
            List<Object> expectedResultPositions = [4, 9, "Flabbergast.groovy"]

        expect:
            testDeclaration(directory, file, position, expectedResultPositions)
    }

    void "Static method should result in func declaration"() {
        given:
            String directory = 'small/funcref/staticfuncref'
            String file = 'StaticFuncRef.groovy'
            List<Integer> position = [5, 20]
            List<Object> expectedResultPositions = [4, 16, "Flabbergast.groovy"]

        expect:
            testDeclaration(directory, file, position, expectedResultPositions)
    }

}
