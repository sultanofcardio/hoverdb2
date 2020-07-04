package com.sultanofcardio.database

import com.sultanofcardio.database.vendor.SQLServer
import kotlin.test.*
import java.sql.ResultSet

class SQLServerTest {

    private val database = SQLServer("localhost", 1433, "sqlserver", "user", "")

    @Test
    fun sqlServerSelectTest() {
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
    fun sqlServerInsertTest() {
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
        sqlServerSelectTest()
    }

    @Test
    fun sqlServerFormatSelect() {
        val select = database.select()
                .from("SOME_TABLE")
                .whereEquals("id", 24)
                .limit(1)
        val selectQuery: String = select.toString()
        assertNotNull(selectQuery)
        println(selectQuery)
        assertEquals("SELECT TOP 1 * FROM SOME_TABLE WHERE id = 24".length.toLong(), selectQuery.length.toLong())
        assertEquals("SELECT TOP 1 * FROM SOME_TABLE WHERE id = 24", selectQuery)
    }
}