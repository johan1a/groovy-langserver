


class ClassRefInClosure {

    void method(){
        def x = { ClassRefInClosure s ->
            return s + "hey"
        }
    }

}