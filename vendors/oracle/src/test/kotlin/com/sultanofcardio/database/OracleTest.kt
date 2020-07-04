package com.sultanofcardio.database

import com.sultanofcardio.database.vendor.Oracle
import java.sql.ResultSet
import java.sql.SQLException
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

class OracleTest {

    private val database = Oracle("localhost", 3307, "oracledb", "user", "")

    @Test
    @Throws(SQLException::class)
    fun oracleSelectTest() {
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
    fun oracleInsertTest() {
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
        oracleSelectTest()
    }

    @Test
    fun oracleFormatSelect() {
        val select = database.select()
                .from("SOME_TABLE")
                .whereEquals("id", 24)
                .limit(1)
        val selectQuery: String = select.toString()
        assertNotNull(selectQuery)
        println(selectQuery)
        assertEquals("SELECT * FROM SOME_TABLE WHERE id = 24 AND ROWNUM <= 1".length.toLong(), selectQuery.length.toLong())
        assertEquals("SELECT * FROM SOME_TABLE WHERE id = 24 AND ROWNUM <= 1", selectQuery)
    }

    @Test
    fun oracleFormatInsert() {
        val insert = database.insert()
                .into("table")
                .value("column1", "value1")
                .value("column2", 0)
                .value("column3", 0.51)
                .value("column4", false)
                .value("column5", Date())
        val selectQuery: String = insert.toString()
        assertNotNull(selectQuery)
        println(selectQuery)
    }
}