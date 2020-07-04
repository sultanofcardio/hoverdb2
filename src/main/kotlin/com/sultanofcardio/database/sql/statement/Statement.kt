@file:Suppress("UNCHECKED_CAST")

package com.sultanofcardio.database.sql.statement

import com.sultanofcardio.database.Database
import com.sultanofcardio.database.escape
import com.sultanofcardio.database.sql.Date
import com.sultanofcardio.database.sql.Literal
import org.intellij.lang.annotations.Language
import java.sql.SQLException
import java.text.SimpleDateFormat

class DatabaseNotInitializedError: RuntimeException("No database has been set on this statement")

/**
 * Abstract representation of a statement in a RDBMS.
 * @author sultanofcardio
 */
abstract class Statement<T: Statement<T>> (var type: Type) {
    var tableName: String? = null
    get() = if(field == null) throw RuntimeException("No table name specified") else field
    var whereConditions: MutableMap<String, Any> = mutableMapOf()
    var stringWhereConditions: MutableList<String> = mutableListOf()
    var database: Database<*>? = null
        get() = if(field == null) throw DatabaseNotInitializedError() else field

    enum class Type {
        /**
         * Data Definition Language Statement
         */
        DDL,

        /**
         * Data Manipulation Language Statement
         */
        DML,

        /**
         * Data Control Language Statement
         */
        DCL,

        /**
         * Transaction Control Statement
         */
        TCS
    }

    fun from(tableName: String): T {
        val name = tableName.escape()
        require(name.isNotEmpty()) { "Table name cannot be empty" }
        this.tableName = tableName.escape()
        return this as T
    }

    /**
     * Add a condition to this statement of the form `WHERE` [column] = [value].
     * `WHERE` will be replaced by `AND` if this is not the first such condition
     */
    fun whereEquals(column: String, value: Any): T {
        whereConditions[column.escape()] = value
        return this as T
    }

    /**
     * Add several conditions to this statement of the form `WHERE` [whereConditions.key] = [whereConditions.value]
     */
    fun where(whereConditions: Map<String, Any>): T {
        whereConditions.forEach { (key, value) ->
            this.whereConditions[key.escape()] = value
        }
        return this as T
    }

    /**
     * Add several conditions to this statement of the form `WHERE` [conditions]
     */
    fun where(@Language("SQL") vararg conditions: String): T {
        stringWhereConditions.addAll(conditions)
        return this as T
    }

    /**
     * Add a condition to this statement of the form `AND` [column] = [value]
     * `AND` will be replaced by `WHERE` if this is the first such condition
     */
    fun andEquals(column: String, value: Any): T {
        whereConditions[column.escape()] = value
        return this as T
    }

    fun and(@Language("SQL") vararg condition: String): T {
        stringWhereConditions.addAll(condition)
        return this as T
    }

    /**
     * Format this Statement object as a valid raw SQL string
     * @return valid SQL code
     */
    protected abstract fun format(): String

    override fun toString(): String  = format()

    /**
     * Run this statement on its internal database
     * @return the result of this statement
     * @see Database.run
     */
    @Throws(SQLException::class)
    open fun run(): Long {
        return database!!.run(this)
    }
}