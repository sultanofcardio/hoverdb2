package com.sultanofcardio.database

import ch.vorburger.mariadb4j.DB
import com.sultanofcardio.database.sql.Literal
import com.sultanofcardio.database.sql.statement.Insert
import com.sultanofcardio.database.sql.statement.Select
import com.sultanofcardio.database.sql.statement.Update
import com.sultanofcardio.database.vendor.MySQL
import org.junit.AfterClass
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test
import java.sql.ResultSet
import java.sql.SQLException

class MySQLTest {
    @Test
    @Throws(SQLException::class)
    fun mysqlSelectTest() {
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
    @Throws(SQLException::class)
    fun mysqlInsertTest() {
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
        mysqlSelectTest()
    }

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

    companion object {
        private var mysql: DB = DB.newEmbeddedDB(3340)
        private val database = MySQL("localhost", 3340, "db", "user", "")

        @BeforeClass
        @JvmStatic
        fun setup() {
            mysql.start()
            mysql.createDB("db", "user", "")
            database.run("""
                CREATE TABLE test_table ( 
                    id INT NOT NULL AUTO_INCREMENT, 
                    words VARCHAR(50), 
                    PRIMARY KEY (id)
                );
            """.trimIndent())
            database.insert()
                    .into("test_table")
                    .value("words", "The time is now ${System.currentTimeMillis()}")
                    .run()
        }

        @AfterClass
        @JvmStatic
        fun tearDown() {
            mysql.stop()
        }
    }
}
