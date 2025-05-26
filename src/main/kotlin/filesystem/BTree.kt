package org.example.filesystem

import io.klogging.NoCoLogging
import kotlin.math.ceil

// non courotines for now, as memeory reading operations are not blocking. Should be changed to
// suspend functions when
// switching to reading from disk
// maybe should be aware of which node it is inside
data class BTreeElement(val value: Int, var left: BTreeNode? = null, var right: BTreeNode? = null) {
    fun childrenValues(): List<List<Int?>> {
        val list = mutableListOf<List<Int?>>()
        list.add(left?.values() ?: emptyList())
        list.add(right?.values() ?: emptyList())
        return list
    }
}

// parent is nullable because of the root
// Should parent be a whole node or a concrete element?
data class BTreeNode(var elements: MutableList<BTreeElement>, var parent: BTreeNode? = null) :
    NoCoLogging {

    override fun toString(): String {
        return "BTreeNode(elements=$elements)"
    }

    fun values(): List<Int> {
        return this.elements.map { it.value }.toList()
    }

    fun childrenValues(): List<List<Int?>> {
        val list = mutableListOf<List<Int?>>()
        this.elements.forEach { list.addAll(it.childrenValues()) }
        return list
    }
}

data class BTree(val root: Int, val maxNodeSize: Int = 4) : NoCoLogging {
    val valueList: BTreeNode = BTreeNode(mutableListOf(BTreeElement(root)))

    fun displayTreeByLevel() {
        logger.debug { "Displaying B-Tree level by level:" }
        if (valueList.elements.isEmpty()) {
            logger.debug { "Tree is empty." }
            return
        }

        val queue = ArrayDeque<BTreeNode>()
        queue.add(valueList)

        val visited = mutableListOf<BTreeNode>()
        visited.add(valueList)

        var level = 0

        while (queue.isNotEmpty()) {
            val nodesAtCurrentLevel = queue.size
            logger.debug { "--- Level $level ---" }

            repeat(nodesAtCurrentLevel) {
                val currentNode = queue.removeFirst()
                logger.debug { "  Node elements: ${currentNode.values()}" }

                for (element in currentNode.elements) {
                    element.left?.let { leftChild ->
                        if (visited.none { it === leftChild }) {
                            logger.debug {
                                "    Left child of ${element.value}: ${leftChild.values()}"
                            }
                            queue.add(leftChild)
                            visited.add(leftChild)
                        }
                    }

                    element.right?.let { rightChild ->
                        if (visited.none { it === rightChild }) {
                            logger.debug {
                                "    Right child of ${element.value}: ${rightChild.values()}"
                            }
                            queue.add(rightChild)
                            visited.add(rightChild)
                        }
                    }
                }
            }
            level++
        }
        logger.debug { "--- End of B-Tree Display ---" }
    }

    fun insertAll(vararg values: Int) {
        values.forEach { insert(it) }
    }

    fun insert(value: Int) {
        logger.debug { "Inserting value into the B-Tree: $value **********************" }
        val newElement = BTreeElement(value, null, null)
        insertTreeElement(newElement)
        logger.debug { "Value $value inserted **********************" }
    }

    // consider returning pair of the node with the index it should be put in
    private fun getInsertionNodeRecursive(
        node: BTreeNode,
        valueToBeInserted: BTreeElement,
    ): BTreeNode {
        val index = node.elements
            .binarySearch { it.value.compareTo(valueToBeInserted.value) }
            .let { if (it < 0) -(it + 1) else it }

        if (index == node.elements.size)
            node.elements[index - 1].right?.let {
                return getInsertionNodeRecursive(it, valueToBeInserted)
            }

        if (index < node.elements.size && valueToBeInserted.value < node.elements[index].value) {
            if (index > 0 && valueToBeInserted.value > node.elements[index - 1].value) {
                node.elements[index].left?.let {
                    return getInsertionNodeRecursive(it, valueToBeInserted)
                }
            } else {
                node.elements[0].left?.let {
                    return getInsertionNodeRecursive(it, valueToBeInserted)
                }
            }
        }
        return node
    }

    private fun insertTreeElement(newElement: BTreeElement) {
        logger.debug { "Current list: ${valueList.values()}" }
        logger.debug { "trying to insert ${newElement.value}" }

        val insertionNode = getInsertionNodeRecursive(valueList, newElement)
        val index = getInsertionIndex(insertionNode, newElement)
        insertionNode.elements.add(index, newElement)

        logger.debug { "Updated list: ${insertionNode.values()}" }
        logger.debug { "Children values: ${insertionNode.childrenValues()}" }

        if (insertionNode.elements.size > maxNodeSize) {
            logger.debug { "********* Node size exceeded, splitting the node *********" }
            logger.debug {
                "Splitting at index ${maxNodeSize / 2}. Value ${insertionNode.elements[maxNodeSize / 2].value} becomes a parent"
            }
            insertionNode.splitNode()
            logger.debug { "********* Split finished *********" }
        }
    }

    // a little duplication of the logic since we get that information inside of the getInsertionNode
    private fun getInsertionIndex(insertionNode: BTreeNode, valueToBeInserted: BTreeElement): Int {
        check(insertionNode.elements.indexOf(valueToBeInserted) == -1) {
            "Cannot insert duplicate values into B-Tree"
        }

        insertionNode.elements.forEachIndexed { index, listValue ->
            if (listValue.value > valueToBeInserted.value) {
                logger.debug { "value $valueToBeInserted should be inserted at index $index" }
                return index
            }
        }
        logger.debug {
            "${valueToBeInserted.value} Should be inserted at the end of ${insertionNode.values()}"
        }
        return insertionNode.elements.size
    }

    private fun BTreeNode.splitNode() {
        check(this.elements.size > maxNodeSize) { "Cannot split a node that is not full" }
        logger.debug { "Splitting a node with values ${values()} }}" }
        logger.debug { "And children values ${childrenValues()}" }
        val setOfChildrenParents = mutableListOf<List<Int>>()
        this.elements.forEach { element ->
            setOfChildrenParents.add(element.left?.parent?.values() ?: emptyList())
            setOfChildrenParents.add(element.right?.parent?.values() ?: emptyList())
        }
        logger.debug { "Children parents: $setOfChildrenParents" }
        val parentNode = parent
        if (parentNode != null) {
            logger.debug { "And parent with values ${parentNode.values()}" }
        }

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
        left.forEach { element ->
            element.left?.parent = newElement.left
            element.right?.parent = newElement.left
        }
        right.forEach { element ->
            element.left?.parent = newElement.right
            element.right?.parent = newElement.right
        }

        logger.debug {
            "new element with value ${newElement.value} and children ${newElement.childrenValues()} left element parent ${newElement.left?.parent?.values()} right element parent ${newElement.right?.parent?.values()}"
        }

        this.elements.clear()
        if (parentNode != null) {
            logger.debug { "Inserting split node back into parent" }
            logger.debug { "Parent node ${parentNode.values()}" }
            parentNode.insertNewElement(newElement)
        } else {
            logger.debug { "Split node was a root node" }
            this.insertNewElement(newElement)
        }
    }

    private fun BTreeNode.insertNewElement(element: BTreeElement) {
        logger.debug { "New element parent node: ${values()}" }
        element.left?.parent = this
        element.right?.parent = this

        val index = getInsertionIndex(this, element)
        //        logger.debug { "Inserting new element $element at index $index" }
        logger.debug {
            "Inserting new element with value ${element.value} and children ${element.childrenValues()} at index $index"
        }
        this.elements.add(index, element)
        if (index > 0) {
            this.elements[index - 1].right = this.elements[index].left
        }
        if (index < elements.size - 1) {
            this.elements[index + 1].left = this.elements[index].right
        }
        if (this.elements.size > maxNodeSize) {
            this.splitNode()
        }
    }

    // perhaps parent could be an element and not a whole node
// only left child implementation now
    fun delete(value: Int) {
        val insertionNode = getInsertionNodeRecursive(valueList, BTreeElement(value, null, null))
        if (!insertionNode.values().contains(value)) {
            logger.debug { "Trying to remove value that is not present in the tree" }
            return
        }

        logger.debug { "removing item $value from the node with values ${insertionNode.values()}" }
        val elementToBeRemoved = insertionNode.elements.find { it.value == value }
        logger.debug { "removing element $elementToBeRemoved" }
        // going from a child to a parent - it has to have elements
        logger.debug { "Node to remove from $insertionNode" }
        logger.debug { "Parent node ${insertionNode.parent}" }
        logger.debug { "Parent elements ${insertionNode.parent!!.values()}" }
        val result = insertionNode.parent!!.elements.find {
            // find the element in a parent whose child's element we try to remove
            // try to find in the right children first, then the left
            it.right?.elements?.contains(elementToBeRemoved) == true
        } ?: insertionNode.parent!!.elements.find {
            it.left?.elements?.contains(elementToBeRemoved) == true
        }
        insertionNode.elements.remove(elementToBeRemoved)
        logger.debug { "Found $result" }
        val minimumElements = ceil(((maxNodeSize / 2)).toDouble())
        logger.debug { "Node size after deletion ${insertionNode.elements.size} and cannot be lower then $minimumElements" }
        if (insertionNode.elements.size < minimumElements) {
            logger.debug { "Size of the node is to small after deletion $insertionNode starting to rebalance" }
            // parent node
            logger.debug { "Parent node ${insertionNode.parent}" }
            // specific element with children
            if (result!!.left!!.elements.size < minimumElements) {
                logger.debug { "Left child need rebalancing" }
                val newLeftElement = insertionNode.parent!!.elements[0]
                logger.debug { "New left element $newLeftElement" }
                val nodeToBorrowFrom = insertionNode.parent!!.elements[0].right
                if (nodeToBorrowFrom!!.elements.size == minimumElements.toInt()) {
                    logger.debug { "Cannot borrow from the right child. Merge is required" }
                    // merge to the parent
                    if (insertionNode.parent!!.elements.size + nodeToBorrowFrom.elements.size + insertionNode.elements.size <= maxNodeSize) {
                        logger.debug { "Merging into the parent node" }
                        insertionNode.parent!!.let { parent ->
                            val tempSet = mutableSetOf<BTreeElement>()
                            logger.debug { "Parent elements ${parent.elements}" }
                            logger.debug { "left child elements ${insertionNode.elements}" }
                            logger.debug { "right child elements ${nodeToBorrowFrom.elements}" }

                            newLeftElement.left = null
                            newLeftElement.right = null

                            tempSet.addAll(parent.elements)
                            tempSet.addAll(insertionNode.elements)
                            tempSet.addAll(nodeToBorrowFrom.elements)

                            logger.debug { "new parent element $tempSet" }
                            parent.elements = tempSet.sortedBy { it.value }.toMutableList()
                        }
                    } // merge siblings
                    else {
                        logger.debug { "Merging sibling nodes" }
                    }
                } else {
                    val promotedElement = nodeToBorrowFrom.elements[0]

                    promotedElement.left = newLeftElement.left
                    promotedElement.right = newLeftElement.right

                    newLeftElement.left = elementToBeRemoved?.left
                    newLeftElement.right = elementToBeRemoved?.right
                    logger.debug { "New left element $newLeftElement" }
//                logger.debug { "New promoted element $promotedElement" }
                    promotedElement.right!!.elements.remove(promotedElement)
                    insertionNode.parent!!.elements.remove(newLeftElement)
                    insertionNode.parent!!.elements.add(promotedElement)
                    // this throws NPE
//                promotedElement.left!!.elements.add(newLeftElement)
                    insertionNode.elements.add(newLeftElement)
                    logger.debug { "Parent after rebalance ${insertionNode.parent!!.elements}" }
                    logger.debug { "left child after rebalance ${insertionNode.parent!!.elements[0].left}" }
                    logger.debug { "right child after rebalance ${insertionNode.parent!!.elements[0].right}" }
                }
            } else {
                logger.debug { "Right child need rebalancing" }
            }


            // borrow from the neighbour
            // find the element whose children we want
//            insertionNode.parent.elements

        }
    }


    fun search(value: Int): BTreeElement? {
        val element = BTreeElement(value, null, null)
        val insertionNode = getInsertionNodeRecursive(valueList, element)
        insertionNode.elements.forEachIndexed { index, listValue ->
            if (listValue.value == value) {
                return listValue
            }
        }
        return null
    }
}
