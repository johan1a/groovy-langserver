


class VarRefInClosureArgument {

    void method(){
        def x = { VarRefInClosureArgument theArgument ->
            return theArgument + "hey"
        }
    }

}