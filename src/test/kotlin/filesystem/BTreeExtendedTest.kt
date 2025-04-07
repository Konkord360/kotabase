package filesystem

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.example.filesystem.BTree
import org.example.filesystem.BTreeElement
import org.example.filesystem.BTreeNode
import org.junit.jupiter.api.DisplayName

class BTreeExtendedTest {

    @Test
    @DisplayName("Test insertion with different tree orders")
    fun testDifferentMaxSizes() {
        // Test with max size 3 (minimum practical size)
        val smallTree = BTree(50, 3)
        // Fill with values that will cause multiple splits
        listOf(25, 75, 10, 30, 60, 90, 5, 15, 20, 35, 55, 65, 80, 95).forEach { smallTree.insert(it) }

        // Tree structure validation
        assertTrue(smallTree.valueList.elements.size <= 3)
        validateNodeSizes(smallTree.valueList, 3)
        validateTreeOrder(smallTree.valueList)

        // Test with larger max size
        val largeTree = BTree(50, 6)
        // Same values
        listOf(25, 75, 10, 30, 60, 90, 5, 15, 20, 35, 55, 65, 80, 95).forEach { largeTree.insert(it) }

        validateNodeSizes(largeTree.valueList, 6)
        validateTreeOrder(largeTree.valueList)
    }

    @Test
    @DisplayName("Test ascending insertion sequence")
    fun testAscendingInsertion() {
        val tree = BTree(1, 4)
        for (i in 2..20) {
            tree.insert(i)
        }

        validateNodeSizes(tree.valueList, 4)
        validateTreeOrder(tree.valueList)
    }

    @Test
    @DisplayName("Test descending insertion sequence")
    fun testDescendingInsertion() {
        val tree = BTree(20, 4)
        for (i in 19 downTo 1) {
            tree.insert(i)
        }

        validateNodeSizes(tree.valueList, 4)
        validateTreeOrder(tree.valueList)
    }

    @Test
    @DisplayName("Test random insertion sequence")
    fun testRandomInsertion() {
        val tree = BTree(50, 4)
        // Deliberately unordered sequence
        listOf(37, 82, 14, 63, 95, 28, 41, 76, 19, 54, 88, 7, 32, 69, 43).forEach {
            tree.insert(it)
        }

        validateNodeSizes(tree.valueList, 4)
        validateTreeOrder(tree.valueList)
    }

    @Test
    @DisplayName("Test parent-child relationships")
    fun testParentChildRelationships() {
        val tree = BTree(50, 4)
        listOf(25, 75, 10, 40, 60, 90).forEach { tree.insert(it) }

        // After these insertions, we should have a split resulting in a structure
        // Manually verify parent-child relationships
        tree.valueList.elements.forEach { element ->
            if (element.left != null) {
                // All values in left child should be less than parent
                element.left!!.elements.forEach { childElement ->
                    assertTrue(childElement.value < element.value)
                }
            }
            if (element.right != null) {
                // All values in right child should be greater than parent
                element.right!!.elements.forEach { childElement ->
                    assertTrue(childElement.value > element.value)
                }
            }
        }
    }

    @Test
    @DisplayName("Test multiple level splits")
    fun testMultipleLevelSplits() {
        // This will create a B-tree with multiple levels
        val tree = BTree(500, 3)
        // Insert enough values to trigger splits at multiple levels
        for (i in 1..100) {
            val value = i * 10
            if (value != 500) {  // Skip the value that's already in the tree
                tree.insert(value)
            }
        }
        // Validate tree height is at least 3
        val height = calculateTreeHeight(tree.valueList)
        assertTrue(height >= 3, "Tree should have at least 3 levels")

        validateNodeSizes(tree.valueList, 3)
        validateTreeOrder(tree.valueList)
    }

    // Helper methods

    private fun validateNodeSizes(node: BTreeNode, maxSize: Int) {
        assertTrue(node.elements.size <= maxSize, "Node size ${node.elements.size} exceeds max $maxSize")

        // Check children recursively
        node.elements.forEach { element ->
            element.left?.let { validateNodeSizes(it, maxSize) }
            element.right?.let { validateNodeSizes(it, maxSize) }
        }
    }

    private fun validateTreeOrder(node: BTreeNode) {
        // Check each element is in order within the node
        for (i in 0 until node.elements.size - 1) {
            assertTrue(node.elements[i].value < node.elements[i + 1].value)
        }

        // Check left subtree elements are smaller than current
        // and right subtree elements are larger
        node.elements.forEachIndexed { index, element ->
            element.left?.let { leftNode ->
                leftNode.elements.forEach { leftElement ->
                    assertTrue(leftElement.value < element.value)
                }
                validateTreeOrder(leftNode)
            }

            element.right?.let { rightNode ->
                rightNode.elements.forEach { rightElement ->
                    if (index < node.elements.size - 1) {
                        assertTrue(rightElement.value < node.elements[index + 1].value)
                    }
                    assertTrue(rightElement.value > element.value)
                }
                validateTreeOrder(rightNode)
            }
        }
    }

    private fun calculateTreeHeight(node: BTreeNode): Int {
        if (node.elements.isEmpty()) return 0

        val element = node.elements.first()
        val leftHeight = element.left?.let { calculateTreeHeight(it) } ?: 0
        val rightHeight = element.right?.let { calculateTreeHeight(it) } ?: 0

        return 1 + maxOf(leftHeight, rightHeight)
    }
}