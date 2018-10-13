package org.gls

import static org.gls.util.TestUtil.testReference

import spock.lang.Specification

@SuppressWarnings(["DuplicateNumberLiteral", "DuplicateListLiteral"])
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

    void "Generic types should result in Class references"() {
        given:
            String directory = 'small/classref1/'
            String file = 'ClassRefInGenericTypes.groovy'
            List<Integer> position = [4, 7]
            List<List<Integer>> expectedResultPositions = [[6, 9]]

        expect:
            testReference(directory, file, position, expectedResultPositions)
    }

    void "Multiple generic types should result in Class references"() {
        given:
            String directory = 'small/classref2/'
            String file = 'MultipleClassRefInGenericTypes.groovy'
            List<Integer> position = [3, 7]
            List<List<Integer>> expectedResultPositions = [[7, 17]]

        expect:
            testReference(directory, file, position, expectedResultPositions)
    }

    void "Declaration generic types should result in Class references"() {
        given:
            String directory = 'small/classref3/'
            String file = 'ClassRefInDeclarationGenericTypes.groovy'
            List<Integer> position = [3, 7]
            List<List<Integer>> expectedResultPositions = [[6, 21]]

        expect:
            testReference(directory, file, position, expectedResultPositions)
    }

    void "Instantiation generic types should result in Class references"() {
        given:
            String directory = 'small/classref4/'
            String file = 'ClassRefInInstantiationGenericTypes.groovy'
            List<Integer> position = [3, 7]
            List<List<Integer>> expectedResultPositions = [[6, 83]]

        expect:
            testReference(directory, file, position, expectedResultPositions)
    }

    void "Return type generic types should result in Class references"() {
        given:
            String directory = 'small/classref5/'
            String file = 'ClassRefInReturnTypeGenericTypes.groovy'
            List<Integer> position = [3, 7]
            List<List<Integer>> expectedResultPositions = [[5, 17]]

        expect:
            testReference(directory, file, position, expectedResultPositions)
    }

    void "Class def should result in Class references"() {
        given:
            String directory = 'small/classref6/'
            String file = 'ClassRefInClassDef.groovy'
            List<Integer> position = [3, 23]
            List<List<Integer>> expectedResultPositions = [[3, 6]]

        expect:
            testReference(directory, file, position, expectedResultPositions)
    }
}
