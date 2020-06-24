package com.sultanofcardio.database

import com.sultanofcardio.database.vendor.H2
import org.junit.Test
import java.sql.ResultSet

class DatabaseTest {

    private val database = H2.Memory("DatabaseTest")

    @Test fun executeStatement(){
        var result: ResultSet? = null
        database.execute("SELECT 'hello world' AS hw") { resultSet ->
            result = resultSet
            println("Result set is closed: ${resultSet.isClosed}")
        }

        println("Result set is closed: ${result?.isClosed}")
    }
}