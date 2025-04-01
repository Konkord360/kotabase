package org.example

import org.example.filesystem.DataManagement
import java.util.Scanner

fun main() {
    val dbman = DataManagement()
    Scanner(System.`in`).use {
        dbman.init()
        println(dbman.listTables())
        // More GOLANG like error handling
        val result = dbman.executeStatement("SELECT data FROM test")
        if (result.error.isNotEmpty()) {
            println("Error: ${result.error}")
        } else {
            println(result.result)
        }
//        val readLine = it.nextLine()!!
//        println("USER INPUT: $readLine")

    }

    println("Hello World!")
}
