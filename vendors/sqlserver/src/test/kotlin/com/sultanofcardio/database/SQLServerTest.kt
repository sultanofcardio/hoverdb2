package com.sultanofcardio.database

import com.sultanofcardio.database.vendor.SQLServer
import org.junit.Assert
import org.junit.Test
import java.sql.ResultSet

class SQLServerTest {

    private val database = SQLServer("localhost", 1433, "sqlserver", "user", "")

    @Test
    fun sqlServerSelectTest() {
        database.select()
                .from("test_table")
                .execute { resultSet: ResultSet ->
                    Assert.assertNotNull(resultSet)
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
        Assert.assertNotEquals(-1, result)
        result = database.insert()
                .into("test_table")
                .value("words", String.format("The time is now %s", System.currentTimeMillis()))
                .run()
        Assert.assertNotEquals(-1, result)
        result = database.insert()
                .into("test_table")
                .value("words", String.format("The time is now %s", System.currentTimeMillis()))
                .run()
        Assert.assertNotEquals(-1, result)
        sqlServerSelectTest()
    }

    @Test
    fun sqlServerFormatSelect() {
        val select = database.select()
                .from("SOME_TABLE")
                .whereEquals("id", 24)
                .limit(1)
        val selectQuery: String = select.toString()
        Assert.assertNotNull(selectQuery)
        println(selectQuery)
        Assert.assertEquals("SELECT TOP 1 * FROM SOME_TABLE WHERE id = 24".length.toLong(), selectQuery.length.toLong())
        Assert.assertEquals("SELECT TOP 1 * FROM SOME_TABLE WHERE id = 24", selectQuery)
    }
}