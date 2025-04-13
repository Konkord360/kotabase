package filesystem

import kotlin.test.Test
import kotlin.test.assertContentEquals
import org.example.filesystem.BTree
import org.example.filesystem.BTreeElement
import org.example.filesystem.BTreeNode
import org.junit.jupiter.api.Assertions.assertTrue

// I should start using kotest
// really need a better way of testing the exact structure
class BTreeTest {

    // try writing some kind of DSL for the tree testing
    @Test
    fun testy() {

        val tree = BTree(2)
        assertContentEquals(listOf(BTreeElement(2)), tree.valueList.elements)
        tree.insert(3)
        assertContentEquals(listOf(BTreeElement(2), BTreeElement(3)), tree.valueList.elements)
        tree.insert(1)
        assertContentEquals(
            listOf(BTreeElement(1), BTreeElement(2), BTreeElement(3)),
            tree.valueList.elements,
        )
        tree.insert(6)
        assertContentEquals(
            listOf(BTreeElement(1), BTreeElement(2), BTreeElement(3), BTreeElement(6)),
            tree.valueList.elements,
        )
        // split
        tree.insert(5)
        println(tree.valueList.elements)
        assertContentEquals(
            listOf(
                    BTreeElement(
                        3,
                        left =
                            BTreeNode(
                                mutableListOf(BTreeElement(1), BTreeElement(2)),
                                tree.valueList,
                            ),
                        right =
                            BTreeNode(
                                mutableListOf(BTreeElement(5), BTreeElement(6)),
                                tree.valueList,
                            ),
                    )
                )
                .toList(),
            tree.valueList.elements,
        )
        tree.insert(4)
        assertContentEquals(
            listOf(
                BTreeElement(
                    3,
                    BTreeNode(mutableListOf(BTreeElement(1), BTreeElement(2)), tree.valueList),
                    BTreeNode(
                        mutableListOf(BTreeElement(4), BTreeElement(5), BTreeElement(6)),
                        tree.valueList,
                    ),
                )
            ),
            tree.valueList.elements,
        )
        tree.insert(-1)
        assertContentEquals(
            listOf(
                BTreeElement(
                    3,
                    left =
                        BTreeNode(
                            mutableListOf(BTreeElement(-1), BTreeElement(1), BTreeElement(2)),
                            tree.valueList,
                        ),
                    right =
                        BTreeNode(
                            mutableListOf(BTreeElement(4), BTreeElement(5), BTreeElement(6)),
                            tree.valueList,
                        ),
                )
            ),
            tree.valueList.elements,
        )
        tree.insert(7)
        assertContentEquals(
            listOf(
                BTreeElement(
                    3,
                    BTreeNode(
                        mutableListOf(BTreeElement(-1), BTreeElement(1), BTreeElement(2)),
                        tree.valueList,
                    ),
                    BTreeNode(
                        mutableListOf(
                            BTreeElement(4),
                            BTreeElement(5),
                            BTreeElement(6),
                            BTreeElement(7),
                        ),
                        tree.valueList,
                    ),
                )
            ),
            tree.valueList.elements,
        )

        tree.insert(8)
        assertContentEquals(
            listOf(
                BTreeElement(
                    3,
                    left =
                        BTreeNode(
                            mutableListOf(BTreeElement(-1), BTreeElement(1), BTreeElement(2)),
                            tree.valueList,
                        ),
                    right =
                        BTreeNode(mutableListOf(BTreeElement(4), BTreeElement(5)), tree.valueList),
                ),
                BTreeElement(
                    6,
                    left =
                        BTreeNode(mutableListOf(BTreeElement(4), BTreeElement(5)), tree.valueList),
                    right =
                        BTreeNode(mutableListOf(BTreeElement(7), BTreeElement(8)), tree.valueList),
                ),
            ),
            tree.valueList.elements,
        )

        assertTrue(tree.valueList.elements[0].right === tree.valueList.elements[1].left)

        tree.insert(9)
        assertContentEquals(
            listOf(
                BTreeElement(
                    3,
                    left =
                        BTreeNode(
                            mutableListOf(BTreeElement(-1), BTreeElement(1), BTreeElement(2)), tree.valueList
                        ),
                    right = BTreeNode(mutableListOf(BTreeElement(4), BTreeElement(5)),tree.valueList),
                ),
                BTreeElement(
                    6,
                    left = BTreeNode(mutableListOf(BTreeElement(4), BTreeElement(5)), tree.valueList),
                    right =
                        BTreeNode(mutableListOf(BTreeElement(7), BTreeElement(8), BTreeElement(9)), tree.valueList),
                ),
            ),
            tree.valueList.elements,
        )
        tree.insert(10)
        assertContentEquals(
            listOf(
                BTreeElement(
                    3,
                    left =
                        BTreeNode(
                            mutableListOf(BTreeElement(-1), BTreeElement(1), BTreeElement(2)), tree.valueList
                        ),
                    right = BTreeNode(mutableListOf(BTreeElement(4), BTreeElement(5)), tree.valueList),
                ),
                BTreeElement(
                    6,
                    left = BTreeNode(mutableListOf(BTreeElement(4), BTreeElement(5)), tree.valueList),
                    right =
                        BTreeNode(
                            mutableListOf(
                                BTreeElement(7),
                                BTreeElement(8),
                                BTreeElement(9),
                                BTreeElement(10),
                            ), tree.valueList
                        ),
                ),
            ),
            tree.valueList.elements,
        )

        tree.insert(11)
        assertContentEquals(
            listOf(
                BTreeElement(
                    3,
                    left =
                        BTreeNode(
                            mutableListOf(BTreeElement(-1), BTreeElement(1), BTreeElement(2)), tree.valueList
                        ),
                    right = BTreeNode(mutableListOf(BTreeElement(4), BTreeElement(5)), tree.valueList),
                ),
                BTreeElement(
                    6,
                    left = BTreeNode(mutableListOf(BTreeElement(4), BTreeElement(5)), tree.valueList),
                    right = BTreeNode(mutableListOf(BTreeElement(7), BTreeElement(8)), tree.valueList),
                ),
                BTreeElement(
                    9,
                    left = BTreeNode(mutableListOf(BTreeElement(7), BTreeElement(8)), tree.valueList),
                    right = BTreeNode(mutableListOf(BTreeElement(10), BTreeElement(11)), tree.valueList),
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
//        assertContentEquals(
//            listOf(
//                // THats what the implementation did but it is sooo wrong
//                // Now it is supposed to pop the 6 back to the parent
//                BTreeElement(
//                    9,
//                    left =
//                        BTreeNode(
//                            mutableListOf(
//                                BTreeElement(
//                                    3,
//                                    left =
//                                        BTreeNode(
//                                            mutableListOf(
//                                                BTreeElement(-1),
//                                                BTreeElement(1),
//                                                BTreeElement(2),
//                                            )
//                                        ),
//                                    right =
//                                        BTreeNode(mutableListOf(BTreeElement(4), BTreeElement(5))),
//                                ),
//                                BTreeElement(
//                                    6,
//                                    left =
//                                        BTreeNode(mutableListOf(BTreeElement(4), BTreeElement(5))),
//                                    right =
//                                        BTreeNode(mutableListOf(BTreeElement(7), BTreeElement(8))),
//                                ),
//                            )
//                        ),
//                    right =
//                        BTreeNode(
//                            mutableListOf(
//                                BTreeElement(
//                                    12,
//                                    left =
//                                        BTreeNode(
//                                            mutableListOf(BTreeElement(10), BTreeElement(11)), tree.valueList.elements[0].right//like this
//                                        ),
//                                    right =
//                                        BTreeNode(mutableListOf(BTreeElement(13), BTreeElement(14))),
//                                ),
//                                BTreeElement(
//                                    15,
//                                    left =
//                                        BTreeNode(
//                                            mutableListOf(BTreeElement(13), BTreeElement(14))
//                                        ),
//                                    right =
//                                        BTreeNode(mutableListOf(BTreeElement(16), BTreeElement(17))),
//                                ),
//                            )
//                        ),
//                )
//            ),
//            tree.valueList.elements,
//        )
    }
}
