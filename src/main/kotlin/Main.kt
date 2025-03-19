package org.example

import org.example.filesystem.DataManagement
import java.util.Scanner

fun main() {
    val dbman = DataManagement()
    Scanner(System.`in`).use {
        dbman.init()
        println(dbman.listTables())
        println(dbman.executeStatement("SELECT data FROM test"))
//        val readLine = it.nextLine()!!
//        println("USER INPUT: $readLine")

    }

    println("Hello World!")
}
