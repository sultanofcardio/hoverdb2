package com.sultanofcardio.database

import com.sultanofcardio.database.sql.Literal
import com.sultanofcardio.database.sql.statement.Insert
import com.sultanofcardio.database.sql.statement.Select
import com.sultanofcardio.database.sql.statement.Update
import com.sultanofcardio.database.vendor.MySQL
import org.junit.Assert
import org.junit.Test

class MySQLFormattingTest {

    private val database = MySQL("localhost", 3340, "db", "user", "")

    @Test
    fun mysqlFormatSelect() {
        val select: Select = database.select()
                .from("SOME_TABLE")
                .whereEquals("id", 24)
        val selectQuery: String = select.toString()
        Assert.assertNotNull(selectQuery)
        println(selectQuery)
        Assert.assertEquals("SELECT * FROM SOME_TABLE WHERE id = 24".length.toLong(), selectQuery.length.toLong())
        Assert.assertEquals("SELECT * FROM SOME_TABLE WHERE id = 24", selectQuery)
    }

    @Test
    fun mysqlFormatSelectLiteral() {
        val select: Select = database.select()
                .from("SOME_TABLE")
                .whereEquals("date", Literal("SYSDATE"))
        val selectQuery: String = select.toString()
        Assert.assertNotNull(selectQuery)
        println(selectQuery)
    }

    @Test
    fun mysqlFormatInsertLiteral() {
        val insert: Insert = database.insert()
                .into("SOME_TABLE")
                .value("date", Literal("SYSDATE"))
        val insertQuery: String = insert.toString()
        Assert.assertNotNull(insertQuery)
        println(insertQuery)
    }

    @Test
    fun mysqlFormatUpdateLiteral() {
        val update: Update = database.update("SOME_TABLE")
                .set("date", Literal("SYSDATE"))
                .whereEquals("date", Literal("SYSDATE"))
        val updateQuery: String = update.toString()
        Assert.assertNotNull(updateQuery)
        println(updateQuery)
    }

    @Test
    fun mysqlFormatDeleteLiteral() {
        val deleteQuery = database.delete()
                .from("SOME_TABLE")
                .whereEquals("date", Literal("SYSDATE"))
                .toString()
        Assert.assertNotNull(deleteQuery)
        println(deleteQuery)
    }

    @Test
    fun formatLiteralTests() {
        mysqlFormatSelectLiteral()
        mysqlFormatInsertLiteral()
        mysqlFormatDeleteLiteral()
        mysqlFormatUpdateLiteral()
    }
}
