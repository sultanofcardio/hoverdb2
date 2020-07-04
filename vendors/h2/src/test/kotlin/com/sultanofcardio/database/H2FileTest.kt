package com.sultanofcardio.database

import com.sultanofcardio.database.vendor.H2
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import kotlin.test.Test
import java.io.File
import java.sql.ResultSet
import java.sql.SQLException
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

class H2FileTest {

    @Test
    @Throws(SQLException::class)
    fun h2DeleteTest() {
        h2InsertTest()
        val deleted = database.delete()
                .from("test_table")
                .whereEquals("id", 1)
                .run()

        assert(deleted == 1L)

        database.select()
                .from("test_table")
                .execute { resultSet: ResultSet ->
                    assertNotNull(resultSet)
                    while (resultSet.next()) {
                        val id = resultSet.getInt("id")
                        val words = resultSet.getString("words")
                        println(String.format("Row{id=%s, words=%s}", id, words))
                    }
                }
    }

    @Test
    @Throws(SQLException::class)
    fun h2UpdateTest() {
        h2InsertTest()
        val updated = database.update("test_table")
                .set("words", "Changed this one")
                .whereEquals("id", 1)
                .run()

        assert(updated == 1L)

        database.select()
                .from("test_table")
                .execute { resultSet: ResultSet ->
                    assertNotNull(resultSet)
                    while (resultSet.next()) {
                        val id = resultSet.getInt("id")
                        val words = resultSet.getString("words")
                        println(String.format("Row{id=%s, words=%s}", id, words))
                    }
                }
    }

    @Test
    @Throws(SQLException::class)
    fun h2SelectTest() {
        h2InsertTest()
        database.select()
                .from("test_table")
                .execute { resultSet: ResultSet ->
                    assertNotNull(resultSet)
                    while (resultSet.next()) {
                        val id = resultSet.getInt("id")
                        val words = resultSet.getString("words")
                        println(String.format("Row{id=%s, words=%s}", id, words))
                    }
                }
    }

    @Test
    @Throws(SQLException::class)
    fun h2InsertTest() {
        var result = database.insert()
                .into("test_table")
                .value("words", String.format("The time is now %s", System.currentTimeMillis()))
                .run()
        assertNotEquals(-1, result)
        result = database.insert()
                .into("test_table")
                .value("words", String.format("The time is now %s", System.currentTimeMillis()))
                .run()
        assertNotEquals(-1, result)
        result = database.insert()
                .into("test_table")
                .value("words", String.format("The time is now %s", System.currentTimeMillis()))
                .run()
        assertNotEquals(-1, result)
    }

    @Test
    fun h2FormatSelect() {
        val select = database.select()
                .from("SOME_TABLE")
                .whereEquals("id", 24)
        val selectQuery = select.toString()
        println(selectQuery)
        assertEquals("SELECT * FROM SOME_TABLE WHERE id = 24".length.toLong(), selectQuery.length.toLong())
        assertEquals("SELECT * FROM SOME_TABLE WHERE id = 24", selectQuery)
    }

    companion object {

        private val database = H2.File(File("h2db"))

        @BeforeAll
        @JvmStatic
        fun setup() {
            database.run("CREATE TABLE test_table( id integer primary key auto_increment, words varchar);")
        }

        @AfterAll
        @JvmStatic
        fun tearDown() {
            database.run("DROP ALL OBJECTS;")
        }
    }
}
