class MultipleFuncRefs1 {



    int intFunc() {
        methodA()

    }


    Set<String> methodB() {
        String a = methodA()
        String b = methodA()
        return methodA()
    }

    List<String> methodA() {
    }
}
