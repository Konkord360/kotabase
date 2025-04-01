package org.example.filesystem

import java.util.LinkedList

val value: MutableList<Int> = LinkedList()

data class BTreeElement(val value: Int, val left: BTreeNode? = null, val right: BTreeNode? = null)

data class BTreeNode(var elements: MutableList<BTreeElement>)

data class TestTree(val left: BTreeNode)

// B-Tree implementation consist of a root node and a list of child nodes
// node has a list of values and a list of child nodes
data class BTree(val root: Int, val maxNodeSize: Int = 4) {
    val valueList: BTreeNode = BTreeNode(mutableListOf(BTreeElement(root, null, null)))

    //    val valueList: MutableList<Int> = mutableListOf(root)

    fun insert(value: Int) {
        // Firstly we go deep
        val newElement = BTreeElement(value, null, null)
        println("Current list: $valueList")
        println("trying to insert $value")
        // this.valueList.add(value)
        // get Insertion index
        var newNode = getInsertionNode(newElement)
        var insertionNode = valueList
        while (newNode != insertionNode) {
            insertionNode = newNode
            newNode = getInsertionNode(newElement)
        }
        //        val insertionNode = getInsertionNode(newElement)
        val index = getInsertionIndex(insertionNode, newElement)
        insertionNode.elements.add(index, newElement)
        println("Updated list: $insertionNode")
        if (insertionNode.elements.size > maxNodeSize) {
            println("Node size exceeded, splitting the node")
            println("Splitting at index ${maxNodeSize/2}")
            insertionNode.split()
        }
    }

    // Probably can be done in a clean way with a recursive function
    private fun getInsertionNode(valueToBeInserted: BTreeElement): BTreeNode {
        valueList.elements.forEach { listValue ->
            if (listValue.value > valueToBeInserted.value) {
                println("Should be inserted at $listValue")
                return listValue.left ?: valueList
            }
            return listValue.right ?: valueList
        }
        return valueList
    }

    // Maybe extend what Index is? Index in a tree and a Node?
    //
    private fun getInsertionIndex(insertionNode: BTreeNode, valueToBeInserted: BTreeElement): Int {
        check(insertionNode.elements.indexOf(valueToBeInserted) == -1) {
            "Cannot insert duplicate values into B-Tree"
        }

        insertionNode.elements.forEachIndexed { index, listValue ->
            if (listValue.value > valueToBeInserted.value) {
                println("Should be inserted at $index")
                return index
            }
        }
        println("Should be inserted at the end")
        return insertionNode.elements.size
    }

    fun delete(value: Int) {
        TODO()
    }

    fun search(value: Int): Int {
        TODO()
    }
} // Insert, Delete, Search operations

// Node has a kid and neighbours
data class Node(val value: List<Int>, val nodes: List<Node>)

data class Leaf(val data: List<String>) {}

// Splitting is kinda ugly with the usage of var - is there any other way to do it?
fun BTreeNode.split() {
    val splitIndex = this.elements.size / 2
    //copies are required in order to not throw ConcurrentModificationException
    val left = this.elements.subList(0, splitIndex).toMutableList()
    val right = this.elements.subList(splitIndex + 1, elements.size).toMutableList()

    this.elements =
        mutableListOf(
            BTreeElement(this.elements[splitIndex].value, BTreeNode(left), BTreeNode(right))
        )
}
// fun BTreeNode.splitWithCopy() {
//    val copyList = this.elements.toMutableList()
//
//    val splitIndex = copyList.size / 2
//    val left = copyList.subList(0, splitIndex)
//    val right = copyList.subList(splitIndex + 1, copyList.size)
//
//    this.elements.clear()
//    this.elements.add(
//        BTreeElement(copyList[splitIndex].value, BTreeNode(left), BTreeNode(right))
//    )
// }
