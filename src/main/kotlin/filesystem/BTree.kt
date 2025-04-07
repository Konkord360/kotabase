package org.example.filesystem

data class BTreeElement(val value: Int, var left: BTreeNode? = null, var right: BTreeNode? = null)

data class BTreeNode(var elements: MutableList<BTreeElement>)

data class BTree(val root: Int, val maxNodeSize: Int = 4) {
    val valueList: BTreeNode = BTreeNode(mutableListOf(BTreeElement(root, null, null)))

    fun insert(value: Int) {
        val newElement = BTreeElement(value, null, null)
        insertTreeElement(newElement)
    }

    // is there a problem when inserting a middle value? Meaning will it have two parents? - yes,
    // but fixed with another function run
    private fun getInsertionNodeRecursive(
        node: BTreeNode,
        valueToBeInserted: BTreeElement,
    ): BTreeNode {
        for (i in node.elements.indices) {
            if (
                i == 0 &&
                valueToBeInserted.value < node.elements[i].value &&
                    node.elements[i].left != null &&
                    node.elements[i].left!!.elements.isNotEmpty()
            ) {
                return getInsertionNodeRecursive(node.elements[i].left!!, valueToBeInserted)
            } else if (
                    i > 0 &&
                    valueToBeInserted.value < node.elements[i].value &&
                    valueToBeInserted.value > node.elements[i - 1].value &&
                    node.elements[i].left != null &&
                    node.elements[i].left!!.elements.isNotEmpty()
                ) {
                    return getInsertionNodeRecursive(node.elements[i].left!!, valueToBeInserted)
            } else if (
                i < node.elements.size - 1 &&
                    valueToBeInserted.value > node.elements[i].value &&
                    valueToBeInserted.value < node.elements[i + 1].value &&
                    node.elements[i].right != null &&
                    node.elements[i].right!!.elements.isNotEmpty()
            ) {
                return getInsertionNodeRecursive(node.elements[i].right!!, valueToBeInserted)
            } else if (
                i == node.elements.size - 1 &&
                    valueToBeInserted.value > node.elements[i].value &&
                    node.elements[i].right != null &&
                    node.elements[i].right!!.elements.isNotEmpty()
            ) {
                return getInsertionNodeRecursive(node.elements[i].right!!, valueToBeInserted)
            }
        }
        return node
    }

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
    //
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

    // split shouldn't really need to insert the element again TODO
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
}
