package com.sultanofcardio.database

import com.sultanofcardio.database.sql.Literal
import kotlin.test.Test
import kotlin.test.assertEquals

class LiteralTest {
    @Test fun format(){
        assertEquals("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua",
        Literal("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua").toString())
    }
}