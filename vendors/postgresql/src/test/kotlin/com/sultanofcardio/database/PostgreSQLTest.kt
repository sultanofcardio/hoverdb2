package com.sultanofcardio.database

import com.sultanofcardio.database.sql.statement.Select
import com.sultanofcardio.database.vendor.PostgreSQL
import org.junit.jupiter.api.TestInstance
import java.net.Socket
import kotlin.test.*
import java.sql.ResultSet
import java.sql.SQLException

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PostgreSQLTest {

    private val database = PostgreSQL("localhost", 5432, "postgres", "postgres",
            "password")

    init {
        database.run("""
            CREATE TABLE test_table(
                id int,
                words varchar
            );
        """.trimIndent())

        database.run("""
            CREATE TABLE some_table(
                id int
            );
        """.trimIndent())
    }

    @Test
    @Throws(SQLException::class)
    fun postgreSQLSelectTest() {
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
    fun postgreSQLInsertTest() {
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
        postgreSQLSelectTest()
    }

    @Test
    fun postgreSQLFormatSelect() {
        val select: Select = database.select()
                .from("SOME_TABLE")
                .whereEquals("id", 24)
        val selectQuery: String = select.toString()
        assertNotNull(selectQuery)
        println(selectQuery)
        assertEquals("SELECT * FROM SOME_TABLE WHERE id = 24".length.toLong(), selectQuery.length.toLong())
        assertEquals("SELECT * FROM SOME_TABLE WHERE id = 24", selectQuery)
    }
}