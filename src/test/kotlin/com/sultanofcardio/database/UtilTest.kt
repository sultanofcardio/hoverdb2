package com.sultanofcardio.database

import com.sultanofcardio.database.sql.Literal
import com.sultanofcardio.database.sql.statement.Select
import kotlin.test.*

class UtilTest {
    @Test fun appendWhereConditions(){
        val select = Select()
                .whereEquals("id", 1)
                .andEquals("email", "someone@example.com")
                .andEquals("unusedField", Literal("null"))

        val string = buildString { appendWhereConditions(select) }
        assertEquals(" ", string.takeLast(1), "There should be a space at the end of the formatted conditions")
        assertEquals("id = 1 AND email = 'someone@example.com' AND unusedField = null ", string)
    }

    @Test fun appendStringWhereConditions(){
        val select = Select()
                .where("id = 1")
                .and("email = 'someone@example.com'")
                .and("unusedField = null")

        val string = buildString { appendStringWhereConditions(select) }
        assertEquals(" ", string.takeLast(1), "There should be a space at the end of the formatted conditions")
        assertEquals("id = 1 AND email = 'someone@example.com' AND unusedField = null ", string)
    }

    @Test fun appendAllSelectConditions(){
        val select = Select()
                .whereEquals("id", 1)
                .andEquals("email", "someone@example.com")
                .andEquals("unusedField", Literal("null"))
                .and("someOtherField is not null")

        val formatted = buildString {
            appendAllConditions(select)
        }

        assertEquals("WHERE id = 1 AND email = 'someone@example.com' AND unusedField = null AND someOtherField is not null ",
        formatted)
    }

    @Test fun properties1(){
        var props = properties { }
        assertTrue(props.isEmpty)

        props = properties {
            put("hello", "world")
        }

        assertFalse(props.isEmpty)
        assertEquals("world", props["hello"])
    }

    @Test fun properties2(){
        var props = properties()
        assertTrue(props.isEmpty)

        props = properties("hello" to "world")

        assertFalse(props.isEmpty)
        assertEquals("world", props["hello"])
    }
}