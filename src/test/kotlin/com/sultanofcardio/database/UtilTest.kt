package com.sultanofcardio.database

import com.sultanofcardio.database.sql.Literal
import com.sultanofcardio.database.sql.statement.Select
import kotlin.test.*

class UtilTest {
    @Test fun appendWhereConditions(){
        val string = buildString {
            appendConditions(mapOf(
                    "id" to 1,
                    "email" to "someone@example.com",
                    "unusedField" to Literal("null")
            ))
        }
        assertEquals(" ", string.takeLast(1), "There should be a space at the end of the formatted conditions")
        assertEquals("id = 1 AND email = 'someone@example.com' AND unusedField = null ", string)
    }

    @Test fun appendStringWhereConditions(){
        val string = buildString {
            appendConditions(listOf(
                    "id = 1",
                    "email = 'someone@example.com'",
                    "unusedField = null"
            ))
        }
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

        println(formatted)
    }
}