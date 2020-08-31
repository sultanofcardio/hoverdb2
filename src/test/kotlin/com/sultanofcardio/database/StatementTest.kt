package com.sultanofcardio.database

import com.sultanofcardio.database.sql.Literal
import com.sultanofcardio.database.sql.statement.Statement
import com.sultanofcardio.database.vendor.H2
import kotlin.test.Test
import kotlin.test.assertEquals

class TestStatement: Statement<TestStatement>(Type.DDL) {

    init {
        database = H2.Memory("statementTest")
    }

    override fun format(): String = "Just for testing"
}

class StatementTest {
    @Test
    fun escape(){
        val statement = TestStatement()
        assertEquals("null", statement.escapeValue(null))
        assertEquals("10", statement.escapeValue(10))
        assertEquals("Hello World", statement.escapeValue("Hello World"))
        assertEquals("Occam''s razor", statement.escapeValue("Occam's razor"))
        assertEquals("Occam's razor", statement.escapeValue(Literal("Occam's razor")))
    }
}