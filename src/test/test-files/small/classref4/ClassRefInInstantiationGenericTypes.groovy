import org.spockframework.util.Pair


class ClassRefInInstantiationGenericTypes {

    void aMethod() {
        Pair<String, ClassRefInInstantiationGenericTypes> aPair = new Pair<String, ClassRefInInstantiationGenericTypes>(null, null)
    }

}