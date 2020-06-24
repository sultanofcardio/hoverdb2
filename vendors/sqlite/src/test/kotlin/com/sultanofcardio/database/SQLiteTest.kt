package com.sultanofcardio.database

import com.sultanofcardio.database.sql.statement.Select
import com.sultanofcardio.database.vendor.SQLite
import org.junit.AfterClass
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test
import java.io.File
import java.sql.SQLException

class SQLiteTest {

    @Test
    @Throws(SQLException::class)
    fun sqliteSelectTest() {
        database.select()
                .from("test_table")
                .execute { resultSet ->
                    Assert.assertNotNull(resultSet)
                    while (resultSet.next()) {
                        val id: Int = resultSet.getInt("id")
                        val words: String = resultSet.getString("words")
                        println(String.format("Row{id=%s, words=%s}", id, words))
                    }
                }
    }

    @Test
    @Throws(SQLException::class)
    fun sqliteInsertTest() {
        var result: Long = database.insert()
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
        sqliteSelectTest()
    }

    @Test
    fun sqliteFormatSelect() {
        val select: Select = database.select()
                .from("SOME_TABLE")
                .whereEquals("id", 24)
                .limit(1)
        val selectQuery: String = select.toString()
        Assert.assertNotNull(selectQuery)
        println(selectQuery)
        Assert.assertEquals("SELECT * FROM SOME_TABLE WHERE id = 24 AND ROWNUM <= 1".length.toLong(), selectQuery.length.toLong())
        Assert.assertEquals("SELECT * FROM SOME_TABLE WHERE id = 24 AND ROWNUM <= 1", selectQuery)
    }

    companion object {

        private val sqlitedb = File("sqlitedb")
        private val database = SQLite(sqlitedb)

        @BeforeClass
        @JvmStatic
        fun setup() {
            if(sqlitedb.exists()) sqlitedb.delete()
            database.run("""
                CREATE TABLE IF NOT EXISTS test_table ( 
                    id INTEGER PRIMARY KEY AUTOINCREMENT, 
                    words VARCHAR(50) 
                )
            """.trimIndent())
            database.insert()
                    .into("test_table")
                    .value("words", "The time is now ${System.currentTimeMillis()}")
                    .run()
        }

        @AfterClass
        @JvmStatic
        fun tearDown() {
            sqlitedb.delete()
        }
    }
}