package org.example.filesystem

import java.util.LinkedList

val value: MutableList<Int> = LinkedList()

data class BTreeElement(val value: Int, var left: BTreeNode? = null, var right: BTreeNode? = null)

data class BTreeNode(var elements: MutableList<BTreeElement>)

data class TestTree(val left: BTreeNode)

// B-Tree implementation consist of a root node and a list of child nodes
// node has a list of values and a list of child nodes
data class BTree(val root: Int, val maxNodeSize: Int = 4) {
    val valueList: BTreeNode = BTreeNode(mutableListOf(BTreeElement(root, null, null)))

    fun insert(value: Int) {
        val newElement = BTreeElement(value, null, null)
        insertTreeElement(newElement)
    }

    // is there a problem when inserting a middle value? Meaning will it have two parents?
    private fun getInsertionNodeRecursive(
        node: BTreeNode,
        valueToBeInserted: BTreeElement,
    ): BTreeNode {
        for (element in node.elements) {
            if (
                valueToBeInserted.value < element.value &&
                    element.left != null &&
                    element.left!!.elements.isNotEmpty()
            ) {
                return getInsertionNodeRecursive(element.left!!, valueToBeInserted)
            } else if (
                valueToBeInserted.value > element.value &&
                    element.right != null &&
                    element.right!!.elements.isNotEmpty()
            ) {
                return getInsertionNodeRecursive(element.right!!, valueToBeInserted)
            }
        }
        return node
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

    private fun insertTreeElement(newElement: BTreeElement) {
        println("Current list: $valueList")
        println("trying to insert $value")

        val insertionNode = getInsertionNodeRecursive(valueList, newElement)
        val index = getInsertionIndex(insertionNode, newElement)
        insertionNode.elements.add(index, newElement)

        println("Updated list: $insertionNode")

        if (insertionNode.elements.size > maxNodeSize) {
            println("Node size exceeded, splitting the node")
            println("Splitting at index ${maxNodeSize/2}")
            insertionNode.splitNode()
            fixChildren(valueList)
        }
    }

    // function that recursively traverses whole binary tree and makes sure that for each element in the node it right child is the same object as the left child of the right neighbour
    private fun fixChildren(node: BTreeNode) {
        node.elements.forEachIndexed { index, element ->
            if (index < node.elements.size - 1) {
                element.right = node.elements[index + 1].left
            }
            if (element.left != null) {
                fixChildren(element.left!!)
            }
            if (element.right != null) {
                fixChildren(element.right!!)
            }
        }
    }

    private fun fixChildren() {

    }

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

    private fun BTreeNode.splitNode() {
        val safeCopy = this.elements.toMutableList()
        val splitIndex = safeCopy.size / 2
        // copies are required in order to not throw ConcurrentModificationException
        val left = safeCopy.subList(0, splitIndex).toMutableList()
        val right = safeCopy.subList(splitIndex + 1, safeCopy.size).toMutableList()

        val newElement = BTreeElement(safeCopy[splitIndex].value, BTreeNode(left), BTreeNode(right))
        // problem is here. I need elements to be null after this function call
        this.elements.clear()
        // we can insert whole new element
        insertTreeElement(newElement)
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

// instead of making a new root of the three, just clear the split part from the main tree, create a
// new node
// and insert it into a tree with new function
// Splitting is kinda ugly with the usage of var - is there any other way to do it?

// Now instead of just splitting and popping the middle value it should add it back to the parent
// list if possible
fun BTreeNode.split() {
    val splitIndex = this.elements.size / 2
    // copies are required in order to not throw ConcurrentModificationException
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
