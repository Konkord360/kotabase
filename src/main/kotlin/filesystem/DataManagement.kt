package org.example.filesystem

import java.io.File
import org.example.ResultObject
import java.io.RandomAccessFile
import java.util.*

class DataManagement {
    private val fileReader = File("data/test")
    private var tables = mutableListOf<Table>()

    fun init() {
        val reader = File("data")
        reader.listFiles()?.forEach { file -> tables.add(Table(file.name)) }
    }

    fun listTables(): List<Table> {
        return tables
    }

    fun createTable(name: String): Boolean {
        return File("data/$name").createNewFile()
    }

    // what about data structure
    // first line columnNames seperated by |
    // rest of the file - data seperated by |
    // Desing for simple data types - string, int, byte
    fun executeStatement(statement: String): ResultObject<String> {
//        val test = TreeMap<String, String>()
        // SELECT/UPDATE/DELETE/TRUNCATE/INSERT
        // WTF Do I do with crazy SQL like select * from (Select * from test where whatever)//recursive parsing?//Crazy regex?
        val tokens = statement.split(" ").map { it.uppercase() }
        return when (tokens[0]) {
            "SELECT" -> {
                if(tokens.size < 4) {
                    return ResultObject("", "SELECT statement must have at least 4 tokens")
                }
                val fields = tokens[1]
                assert(tokens[2] == "FROM")
                val table = Table(tokens[3].lowercase())
                return if (fields == "*") {
                    ResultObject(table.readAll(), "")
                } else {
                    ResultObject(table.readColumn(fields), "")
                }
            }
            "INSERT" -> {
                ResultObject("", "Not implemented")
            }
            "UPDATE" -> {
                ResultObject("", "Not implemented")
            }
            "TRUNCATE" -> {
                ResultObject("", "Not implemented")
            }
            "DELETE" -> {
                ResultObject("", "Not implemented")
            }

            else -> ResultObject("", "Unknown SQL OPERATION ${tokens[0]}")
        }
    }

    fun writeData(data: String) {
        fileReader.writeText(data)
    }

    fun readData(): String {
        return fileReader.readText()
    }

    fun dropFile(): Boolean {
        return fileReader.delete()
    }
}

data class Table(val name: String) {
    private val fileReader = File("data/$name")

    fun readAll(): String {
        return fileReader.readText()
    }

    fun readColumn(columnName: String): String {
        val lines = fileReader.readLines()
        val returnString = StringBuilder()

        // columns
        val columnIndex = lines[0].split("|").indexOf(columnName)

        if (columnIndex == -1) {
            throw UnsupportedOperationException("Column $columnName not found in ")
        }
        for (index in lines.indices) {
            returnString.append(lines[index].split("|")[columnIndex])
            if (index != lines.size - 1) {
                returnString.append("\n")
            }
        }
        println("Prepared response $returnString")

        println("Found $columnName at index $columnIndex")

        return returnString.toString()
    }
}
