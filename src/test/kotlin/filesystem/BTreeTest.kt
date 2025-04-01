package filesystem

import kotlin.test.Test
import kotlin.test.assertEquals
import org.example.filesystem.BTree
import org.example.filesystem.BTreeElement
import org.example.filesystem.BTreeNode

class BTreeTest {

    @Test
    fun testy() {
        // Add a test to validate proper functioning with different maxChildren values// Like for 2
        // and 500
        val tree = BTree(2)
        assertEquals(listOf(BTreeElement(2)), tree.valueList.elements)
        tree.insert(3)
        assertEquals(listOf(BTreeElement(2), BTreeElement(3)), tree.valueList.elements)
        tree.insert(1)
        assertEquals(
            listOf(BTreeElement(1), BTreeElement(2), BTreeElement(3)),
            tree.valueList.elements,
        )
        tree.insert(6)
        assertEquals(
            listOf(BTreeElement(1), BTreeElement(2), BTreeElement(3), BTreeElement(6)),
            tree.valueList.elements,
        )
        // split
        tree.insert(5)
        // first list should be one element with value 3
        // The element should have two children: Lists with elements of values 1,2 at left and 5,6
        // at right // Should "pop" value 3 to the parent node
        assertEquals(
            listOf(
                //                BTreeElement(1),
                //                BTreeElement(2),
                BTreeElement(
                    3,
                    BTreeNode(mutableListOf(BTreeElement(1), BTreeElement(2))),
                    BTreeNode(mutableListOf(BTreeElement(5), BTreeElement(6))),
                )
                //                BTreeElement(5),
                //                BTreeElement(6),
            ),
            tree.valueList.elements,
        )

        tree.insert(4)
        //        Thread.sleep(100)
        assertEquals(
            listOf(
                //                BTreeElement(1),
                //                BTreeElement(2),
                BTreeElement(
                    3,
                    BTreeNode(mutableListOf(BTreeElement(1), BTreeElement(2))),
                    BTreeNode(mutableListOf(BTreeElement(4), BTreeElement(5), BTreeElement(6))),
                )
                //                BTreeElement(5),
                //                BTreeElement(6),
            ),
            tree.valueList.elements,
        )

        tree.insert(-1)
        //        Thread.sleep(100)
        assertEquals(
            listOf(
                //                BTreeElement(1),
                //                BTreeElement(2),
                BTreeElement(
                    3,
                    BTreeNode(mutableListOf(BTreeElement(-1), BTreeElement(1), BTreeElement(2))),
                    BTreeNode(mutableListOf(BTreeElement(4), BTreeElement(5), BTreeElement(6))),
                )
                //                BTreeElement(5),
                //                BTreeElement(6),
            ),
            tree.valueList.elements,
        )

        tree.insert(7)
        //        Thread.sleep(100)
        assertEquals(
            listOf(
                //                BTreeElement(1),
                //                BTreeElement(2),
                BTreeElement(
                    3,
                    BTreeNode(mutableListOf(BTreeElement(-1), BTreeElement(1), BTreeElement(2))),
                    BTreeNode(
                        mutableListOf(
                            BTreeElement(4),
                            BTreeElement(5),
                            BTreeElement(6),
                            BTreeElement(7),
                        )
                    ),
                )
                //                BTreeElement(5),
                //                BTreeElement(6),
            ),
            tree.valueList.elements,
        )

        tree.insert(8)
        //        Thread.sleep(100)
        assertEquals(
            listOf(
                // THats what the implementation did but it is sooo wrong
                BTreeElement(
                    3,
                    left = BTreeNode(mutableListOf(BTreeElement(-1), BTreeElement(1), BTreeElement(2))),
                    right = BTreeNode(
                        mutableListOf(
                            BTreeElement(
                                6,
                                left = BTreeNode(mutableListOf(BTreeElement(4), BTreeElement(5))),
                                right = BTreeNode(mutableListOf(BTreeElement(7), BTreeElement(8))),
                            )
                        )
                    ),
                )
                //                BTreeElement(5),
                //                BTreeElement(6),
            ),
            tree.valueList.elements,
        )
        //        println(tree.valueList)
        //        tree.valueList.elements.forEach { println(it.value) }
        //        val exception = assertThrows<IllegalStateException> {  tree.insert(3) }
        //        assertEquals("Cannot insert duplicate values into B-Tree", exception.message)
    }
}
