package filesystem

import kotlin.test.assertEquals
import org.example.ResultObject
import org.example.filesystem.DataManagement
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DataManagementTest {

    val dataManagement: DataManagement = DataManagement()

    @BeforeEach
    fun init() {
        dataManagement.writeData(
            """
            DATA|DATA2
            HELLO THERE|NOT ME
            """
                .trimIndent()
        )
    }

    @Test
    fun selectAll() {
        assertEquals(
            ResultObject(
                """
                DATA|DATA2
                HELLO THERE|NOT ME
                """
                    .trimIndent(),
                "",
            ),
            dataManagement.executeStatement("SELECT * FROM test"),
        )
    }

    @Test
    fun selectFirstColumn() {
        assertEquals(
            ResultObject(
                """
            DATA
            HELLO THERE
            """
                    .trimIndent(),
                "",
            ),
            dataManagement.executeStatement("SELECT data FROM test"),
        )
    }

    @Test
    fun selectSecondColumn() {
        assertEquals(
            ResultObject(
                """
            DATA2
            NOT ME
            """
                    .trimIndent(),
                "",
            ),
            dataManagement.executeStatement("SELECT data2 FROM test"),
        )
    }
    //    @Test
    //    fun test2() {
    //        println(dataManagement.readData())
    //    }
    //
    //    @Test
    //    fun test3() {
    //        assertTrue(dataManagement.dropFile())
    //    }
}
