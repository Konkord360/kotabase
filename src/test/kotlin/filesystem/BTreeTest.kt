package filesystem

import kotlin.test.Test
import kotlin.test.assertEquals
import org.example.filesystem.BTree
import org.example.filesystem.BTreeElement
import org.example.filesystem.BTreeNode
import org.junit.jupiter.api.Assertions.assertTrue

class BTreeTest {

    // try writing some kind of DSL for the tree testing
    @Test
    fun testy() {
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
        assertEquals(
            listOf(
                BTreeElement(
                    3,
                    BTreeNode(mutableListOf(BTreeElement(1), BTreeElement(2))),
                    BTreeNode(mutableListOf(BTreeElement(5), BTreeElement(6))),
                )
            ),
            tree.valueList.elements,
        )
        tree.insert(4)
        assertEquals(
            listOf(
                BTreeElement(
                    3,
                    BTreeNode(mutableListOf(BTreeElement(1), BTreeElement(2))),
                    BTreeNode(mutableListOf(BTreeElement(4), BTreeElement(5), BTreeElement(6))),
                )
            ),
            tree.valueList.elements,
        )
        tree.insert(-1)
        assertEquals(
            listOf(
                BTreeElement(
                    3,
                    left =
                        BTreeNode(
                            mutableListOf(BTreeElement(-1), BTreeElement(1), BTreeElement(2))
                        ),
                    right =
                        BTreeNode(mutableListOf(BTreeElement(4), BTreeElement(5), BTreeElement(6))),
                )
            ),
            tree.valueList.elements,
        )
        tree.insert(7)
        assertEquals(
            listOf(
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
            ),
            tree.valueList.elements,
        )

        tree.insert(8)
        assertEquals(
            listOf(
                BTreeElement(
                    3,
                    left =
                        BTreeNode(
                            mutableListOf(BTreeElement(-1), BTreeElement(1), BTreeElement(2))
                        ),
                    right = BTreeNode(mutableListOf(BTreeElement(4), BTreeElement(5))),
                ),
                BTreeElement(
                    6,
                    left = BTreeNode(mutableListOf(BTreeElement(4), BTreeElement(5))),
                    right = BTreeNode(mutableListOf(BTreeElement(7), BTreeElement(8))),
                ),
            ),
            tree.valueList.elements,
        )

        assertTrue(tree.valueList.elements[0].right === tree.valueList.elements[1].left)

        tree.insert(9)
        assertEquals(
            listOf(
                BTreeElement(
                    3,
                    left =
                        BTreeNode(
                            mutableListOf(BTreeElement(-1), BTreeElement(1), BTreeElement(2))
                        ),
                    right = BTreeNode(mutableListOf(BTreeElement(4), BTreeElement(5))),
                ),
                BTreeElement(
                    6,
                    left = BTreeNode(mutableListOf(BTreeElement(4), BTreeElement(5))),
                    right =
                        BTreeNode(mutableListOf(BTreeElement(7), BTreeElement(8), BTreeElement(9))),
                ),
            ),
            tree.valueList.elements,
        )
        tree.insert(10)
        assertEquals(
            listOf(
                BTreeElement(
                    3,
                    left =
                        BTreeNode(
                            mutableListOf(BTreeElement(-1), BTreeElement(1), BTreeElement(2))
                        ),
                    right = BTreeNode(mutableListOf(BTreeElement(4), BTreeElement(5))),
                ),
                BTreeElement(
                    6,
                    left = BTreeNode(mutableListOf(BTreeElement(4), BTreeElement(5))),
                    right =
                        BTreeNode(
                            mutableListOf(
                                BTreeElement(7),
                                BTreeElement(8),
                                BTreeElement(9),
                                BTreeElement(10),
                            )
                        ),
                ),
            ),
            tree.valueList.elements,
        )

        tree.insert(11)
        assertEquals(
            listOf(
                BTreeElement(
                    3,
                    left =
                        BTreeNode(
                            mutableListOf(BTreeElement(-1), BTreeElement(1), BTreeElement(2))
                        ),
                    right = BTreeNode(mutableListOf(BTreeElement(4), BTreeElement(5))),
                ),
                BTreeElement(
                    6,
                    left = BTreeNode(mutableListOf(BTreeElement(4), BTreeElement(5))),
                    right = BTreeNode(mutableListOf(BTreeElement(7), BTreeElement(8))),
                ),
                BTreeElement(
                    9,
                    left = BTreeNode(mutableListOf(BTreeElement(7), BTreeElement(8))),
                    right = BTreeNode(mutableListOf(BTreeElement(10), BTreeElement(11))),
                ),
            ),
            tree.valueList.elements,
        )
        tree.insert(12)
        tree.insert(13)
        tree.insert(14)
        // pop
        tree.insert(15)
        tree.insert(16)
        tree.insert(17)
        // double pop
        assertEquals(
            listOf(
                // THats what the implementation did but it is sooo wrong
                // Now it is supposed to pop the 6 back to the parent
                BTreeElement(
                    9,
                    left =
                        BTreeNode(
                            mutableListOf(
                                BTreeElement(
                                    3,
                                    left =
                                        BTreeNode(
                                            mutableListOf(
                                                BTreeElement(-1),
                                                BTreeElement(1),
                                                BTreeElement(2),
                                            )
                                        ),
                                    right =
                                        BTreeNode(mutableListOf(BTreeElement(4), BTreeElement(5))),
                                ),
                                BTreeElement(
                                    6,
                                    left =
                                        BTreeNode(mutableListOf(BTreeElement(4), BTreeElement(5))),
                                    right =
                                        BTreeNode(mutableListOf(BTreeElement(7), BTreeElement(8))),
                                ),
                            )
                        ),
                    right =
                        BTreeNode(
                            mutableListOf(
                                BTreeElement(
                                    12,
                                    left =
                                        BTreeNode(
                                            mutableListOf(BTreeElement(10), BTreeElement(11))
                                        ),
                                    right =
                                        BTreeNode(mutableListOf(BTreeElement(13), BTreeElement(14))),
                                ),
                                BTreeElement(
                                    15,
                                    left =
                                        BTreeNode(
                                            mutableListOf(BTreeElement(13), BTreeElement(14))
                                        ),
                                    right =
                                        BTreeNode(mutableListOf(BTreeElement(16), BTreeElement(17))),
                                ),
                            )
                        ),
                )
            ),
            tree.valueList.elements,
        )
    }
}
