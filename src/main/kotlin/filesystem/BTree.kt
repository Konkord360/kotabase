package org.example.filesystem

import java.util.*

data class BTreeElement(val value: Int, var left: BTreeNode? = null, var right: BTreeNode? = null)

// parent is nullable because of the root
// Should parent be a whole node or a concrete element?
data class BTreeNode(var elements: MutableList<BTreeElement>, var parent: BTreeNode? = null) {
    override fun toString(): String {
        return "BTreeNode(elements=$elements)"
    }
}

data class BTree(val root: Int, val maxNodeSize: Int = 4) {
    val valueList: BTreeNode = BTreeNode(mutableListOf(BTreeElement(root)))

    fun insert(value: Int) {
        val newElement = BTreeElement(value, null, null)
        insertTreeElement(newElement)
    }

    // how does this even work lol?
    private fun getInsertionNodeRecursive(
        node: BTreeNode,
        valueToBeInserted: BTreeElement,
    ): BTreeNode {
        // Find the appropriate index based on value
        val index =
            node.elements
                .binarySearch { it.value.compareTo(valueToBeInserted.value) }
                .let { if (it < 0) -(it + 1) else it }

        // If value is less than current element
        if (index < node.elements.size && valueToBeInserted.value < node.elements[index].value) {
            val childNode =
                if (index > 0) {
                    // Between two elements
                    if (valueToBeInserted.value > node.elements[index - 1].value) {
                        node.elements[index].left
                        // wtf?
                    } else {
                        node.elements[0].left
                    }
                } else {
                    // Smaller than first element
                    node.elements[0].left
                }

            // If child exists, recurse down
            if (childNode != null && childNode.elements.isNotEmpty()) {
                return getInsertionNodeRecursive(childNode, valueToBeInserted)
            }
        }
        // If value is greater than current element
        else if (index > 0 && valueToBeInserted.value > node.elements[index - 1].value) {
            val childNode =
                if (index < node.elements.size) {
                    // Between two elements
                    if (valueToBeInserted.value < node.elements[index].value) {
                        node.elements[index - 1].right
                        // why that else? wtf? bigger then the previoius one and bigger then the
                        // current goes to the left?
                    } else {
                        node.elements[index].left
                    }
                } else {
                    // Greater than last element
                    node.elements[index - 1].right
                }

            // If child exists, recurse down
            if (childNode != null && childNode.elements.isNotEmpty()) {
                return getInsertionNodeRecursive(childNode, valueToBeInserted)
            }
        }

        // If we get here, this is the insertion node
        return node
    }

    // is there a problem when inserting a middle value? Meaning will it have two parents? - yes,
    // but fixed with another function run
    //    private fun getInsertionNodeRecursive(
    //        node: BTreeNode,
    //        valueToBeInserted: BTreeElement,
    //    ): BTreeNode {
    //        for (i in node.elements.indices) {
    //            if (
    //                i == 0 &&
    //                valueToBeInserted.value < node.elements[i].value &&
    //                    node.elements[i].left != null &&
    //                    node.elements[i].left!!.elements.isNotEmpty()
    //            ) {
    //                return getInsertionNodeRecursive(node.elements[i].left!!, valueToBeInserted)
    //            } else if (
    //                    i > 0 &&
    //                    valueToBeInserted.value < node.elements[i].value &&
    //                    valueToBeInserted.value > node.elements[i - 1].value &&
    //                    node.elements[i].left != null &&
    //                    node.elements[i].left!!.elements.isNotEmpty()
    //                ) {
    //                    return getInsertionNodeRecursive(node.elements[i].left!!,
    // valueToBeInserted)
    //            } else if (
    //                i < node.elements.size - 1 &&
    //                    valueToBeInserted.value > node.elements[i].value &&
    //                    valueToBeInserted.value < node.elements[i + 1].value &&
    //                    node.elements[i].right != null &&
    //                    node.elements[i].right!!.elements.isNotEmpty()
    //            ) {
    //                return getInsertionNodeRecursive(node.elements[i].right!!, valueToBeInserted)
    //            } else if (
    //                i == node.elements.size - 1 &&
    //                    valueToBeInserted.value > node.elements[i].value &&
    //                    node.elements[i].right != null &&
    //                    node.elements[i].right!!.elements.isNotEmpty()
    //            ) {
    //                return getInsertionNodeRecursive(node.elements[i].right!!, valueToBeInserted)
    //            }
    //        }
    //        return node
    //    }

    private fun insertTreeElement(newElement: BTreeElement) {
        println("Current list: $valueList")
        println("trying to insert ${newElement.value}")

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

    // Fix shouldn't be needed - split should handle that - maybe tree should split its nodes and
    // not node itself
    // rethink the implementation to maybe preferably not need this shiet
    private fun fixChildren(node: BTreeNode) {
        node.elements.forEachIndexed { index, element ->
            if (index < node.elements.size - 1) {
                if (
                    node.elements[index + 1].left != null &&
                        node.elements[index + 1].left!!.elements.isNotEmpty()
                ) {
                    element.right = node.elements[index + 1].left
                } else if (
                    node.elements[index].right != null &&
                        node.elements[index].right!!.elements.isNotEmpty()
                ) {
                    node.elements[index + 1].left = element.right
                }
            }
            if (element.left != null && element.left!!.elements.isNotEmpty()) {
                fixChildren(element.left!!)
            }
            if (element.right != null && element.right!!.elements.isNotEmpty()) {
                fixChildren(element.right!!)
            }
        }
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
        check(this.elements.size > maxNodeSize) { "Cannot split a node that is not full" }

        val safeCopy = this.elements.toMutableList()
        val splitIndex = safeCopy.size / 2
        // copies are required in order to not throw ConcurrentModificationException
        val left = safeCopy.subList(0, splitIndex).toMutableList()
        val right = safeCopy.subList(splitIndex + 1, safeCopy.size).toMutableList()

        val newElement =
            BTreeElement(
                safeCopy[splitIndex].value,
                BTreeNode(left, this.parent),
                BTreeNode(right, this.parent),
            )

        this.elements.clear()
        if (parent != null) {
            parent!!.insertNewElement(newElement)
        } else {
            this.insertNewElement(newElement)
        }
    }

    private fun BTreeNode.insertNewElement(element: BTreeElement) {
        element.left?.parent = this
        element.right?.parent = this

        val index = getInsertionIndex(this, element)
        this.elements.add(index, element)
        if (this.elements.size > maxNodeSize) {
            this.splitNode()
        }
    }

    fun delete(value: Int) {
        TODO()
    }

    fun search(value: Int): BTreeElement? {
        val newNode = BTreeElement(value, null, null)
        val insertionNode = getInsertionNodeRecursive(valueList, newNode)
        insertionNode.elements.forEachIndexed { index, listValue ->
            if (listValue.value == value) {
                return listValue
            }
        }
        return null
    }
}
