package com.sultanofcardio.database

import com.sultanofcardio.database.vendor.H2
import org.junit.jupiter.api.TestInstance
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

data class Programmer(val name: String, val age: Int, val faveLanguage: String)

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExtensionsTest {
    private val database = H2.Memory("extensionTests")

    open class A
    open class B: A()
    class C: B()

    init {
        database.run("""
            CREATE TABLE programmers( 
              name varchar2,
              age int,
              fave_language varchar2
            );
        """.trimIndent())

        database.insert()
                .into("programmers")
                .value("name", "Jake Wharton")
                .value("age", 30)
                .value("fave_language", "Kotlin")
                .run()

        database.insert()
                .into("programmers")
                .value("name", "Joshua Bloch")
                .value("age", 58)
                .value("fave_language", "Java")
                .run()
    }

    @Test fun list(){
        database.select()
                .from("programmers")
                .execute { resultSet ->
                    val programmers = resultSet.list { Programmer(it.getString("name"), it.getInt("age"), it.getString("fave_language")) }
                    assertFalse(programmers.isEmpty())
                    assertEquals("Jake Wharton", programmers.first().name)
                    assertEquals("Joshua Bloch", programmers.last().name)
                }
    }

    @Test fun set(){
        database.select()
                .from("programmers")
                .execute { resultSet ->
                    val programmers = resultSet.set { Programmer(it.getString("name"), it.getInt("age"), it.getString("fave_language")) }
                    assertFalse(programmers.isEmpty())
                    assertEquals("Jake Wharton", programmers.first().name)
                    assertEquals("Joshua Bloch", programmers.last().name)
                }
    }

    @Test fun prepareStatement(){
        database.connection { connection ->
            connection.prepareStatement("SELECT * FROM programmers") { statement ->
                val resultSet = statement.executeQuery()
                assertTrue(resultSet.next())
                val jake = Programmer(resultSet.getString("name"), resultSet.getInt("age"), resultSet.getString("fave_language"))
                assertEquals("Jake Wharton", jake.name)

                assertTrue(resultSet.next())
                val josh = Programmer(resultSet.getString("name"), resultSet.getInt("age"), resultSet.getString("fave_language"))
                assertEquals("Joshua Bloch", josh.name)
            }
        }
    }

    @Test fun derivesFrom(){
        assertTrue("" derivesFrom String::class)
        assertTrue(B() derivesFrom A::class)
        assertTrue(C() derivesFrom B::class)
        assertTrue(C() derivesFrom A::class)
        assertTrue(A() derivesFrom Any::class)
    }
}
