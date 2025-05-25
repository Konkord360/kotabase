package filesystem

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
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
                        BTreeNode(
                            mutableListOf(BTreeElement(7), BTreeElement(8), BTreeElement(9)),
                            tree.valueList,
                        ),
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
                        BTreeNode(
                            mutableListOf(
                                BTreeElement(7),
                                BTreeElement(8),
                                BTreeElement(9),
                                BTreeElement(10),
                            ),
                            tree.valueList,
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
                BTreeElement(
                    9,
                    left =
                        BTreeNode(mutableListOf(BTreeElement(7), BTreeElement(8)), tree.valueList),
                    right =
                        BTreeNode(mutableListOf(BTreeElement(10), BTreeElement(11)), tree.valueList),
                ),
            ),
            tree.valueList.elements,
        )
        tree.insertAll(12, 13, 14, 15, 16, 17)
        // double pop
        tree.displayTreeByLevel()
        val searchResult = tree.search(12)
        assertEquals(12, searchResult!!.value)
        assertEquals(10, searchResult.left!!.values()[0])
        assertEquals(13, searchResult.right!!.values()[0])
    }

    @Test
    fun testSplit() {
        val tree = BTree(4)
        tree.insertAll(1, 2, 3, 5)
        tree.displayTreeByLevel()
        tree.valueList.values() shouldContainExactly listOf(3)
        tree.valueList.elements[0].left!!.values() shouldBe listOf(1, 2)
        tree.valueList.elements[0].right!!.values() shouldBe listOf(4, 5)
        tree.valueList.elements[0].left!!.parent shouldBe tree.valueList
        tree.valueList.elements[0].right!!.parent shouldBe tree.valueList
        tree.valueList.elements[0].right!!.parent shouldBe tree.valueList.elements[0].left!!.parent
        tree.insertAll(6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16)
        // validate just before splits
        tree.displayTreeByLevel()
        tree.valueList.values() shouldContainExactly listOf(3, 6, 9, 12)
        tree.valueList.parent shouldBe null
        tree.valueList.elements.forEach { element ->
            element.left!!.parent shouldBe tree.valueList
            element.right!!.parent shouldBe tree.valueList
        }
        with(tree.valueList) {
            elements[0].left!!.values() shouldContainExactly listOf(1, 2)
            elements[0].right!!.values() shouldContainExactly listOf(4, 5)
            elements[1].left!!.values() shouldContainExactly listOf(4, 5)
            elements[1].right!!.values() shouldContainExactly listOf(7, 8)
            elements[2].left!!.values() shouldContainExactly listOf(7, 8)
            elements[2].right!!.values() shouldContainExactly listOf(10, 11)
            elements[3].left!!.values() shouldContainExactly listOf(10, 11)
            elements[3].right!!.values() shouldContainExactly listOf(13, 14, 15, 16)

            elements[0].left!!.parent shouldBe tree.valueList
            elements[0].right!!.parent shouldBe tree.valueList
            elements[1].left!!.parent shouldBe tree.valueList
            elements[1].right!!.parent shouldBe tree.valueList
            elements[2].left!!.parent shouldBe tree.valueList
            elements[2].right!!.parent shouldBe tree.valueList
            elements[3].left!!.parent shouldBe tree.valueList
            elements[3].right!!.parent shouldBe tree.valueList
        }
        // another pop
        tree.insert(17)
        tree.displayTreeByLevel()
        tree.valueList.values() shouldContainExactly listOf(9)
        tree.valueList.parent shouldBe null
        with(tree.valueList) {
            elements[0].left!!.parent shouldBe tree.valueList
            elements[0].right!!.parent shouldBe tree.valueList
            elements[0].left!!.values() shouldContainExactly listOf(3, 6)
            elements[0].right!!.values() shouldContainExactly listOf(12, 15)
            elements[0].left!!.elements[0].value shouldBe 3
            elements[0].left!!.elements[0].left!!.values() shouldContainExactly listOf(1, 2)
            elements[0].left!!.elements[0].right!!.values() shouldContainExactly listOf(4, 5)
            // parent is all kinds of fucked up after the second split -- still pointing at the previous one
            elements[0].left!!.elements[0].left!!.parent!!.values() shouldContainExactly listOf(3, 6)
        }
    }

//    @Test
    fun testDeletion() {
        val tree = BTree(4)
        tree.insertAll(-1, 1, 2, 3, 5, 6)
        tree.displayTreeByLevel()
        tree.delete(6)
        tree.valueList.values() shouldContainInOrder listOf(2)
        tree.valueList.elements[0].left!!.values() shouldContainExactly listOf(-1, 1)
        tree.valueList.elements.forEach {
            it.left!!.parent shouldBe tree.valueList
            it.right!!.parent shouldBe tree.valueList

        }
        tree.valueList.elements[0].right!!.values() shouldContainExactly listOf(3, 4, 5)
        // rebalance after removing too many elements
        tree.delete(1)
        tree.valueList.values() shouldContainInOrder listOf(3)
        tree.valueList.elements[0].left!!.values() shouldContainExactly listOf(-1, 2)
        tree.valueList.elements[0].right!!.values() shouldContainExactly listOf(4, 5)
        tree.valueList.elements.forEach {
            it.left!!.parent shouldBe tree.valueList
            it.right!!.parent shouldBe tree.valueList

        }
        // merging and recursive popping
        tree.delete(2)

        tree.valueList.values() shouldContainInOrder listOf(-1, 3, 4, 5)
        tree.valueList.elements.forEach { element ->
            element.left shouldBe null
            element.right shouldBe null
        }
        tree.displayTreeByLevel()
        tree.insertAll(6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25)
        tree.displayTreeByLevel()

        tree.delete(9)

//        tree.valueList.elements[0].left!!.values() shouldContainExactly listOf(-1, 3)
//        tree.valueList.elements[0].right!!.values() shouldContainExactly listOf(5)
        // what should happen - one node with 4 keys or one nod with one left children
        // recursive pop
        // recursive merge
    }
}
