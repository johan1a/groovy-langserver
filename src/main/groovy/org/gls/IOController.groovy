package org.gls

class IOController {

  def start() {
    println "Starting controller"
    System.in.eachLine() { line ->
    if(line.equals("exit"))
        System.exit(0)
    else
        println "you entered: $line"
    }
  }

}
