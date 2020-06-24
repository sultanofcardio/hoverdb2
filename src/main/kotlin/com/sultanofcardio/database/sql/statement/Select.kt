package com.sultanofcardio.database.sql.statement

import com.sultanofcardio.database.interfaces.ResultSetHandler
import org.intellij.lang.annotations.Language
import java.sql.ResultSet
import java.sql.SQLException

/**
 * Class representing an instance of an SQL select query
</T> */
class Select(var isDistinct: Boolean = false, vararg columns: String) : Statement<Select>(Type.DML) {
    var columns: Array<String> = arrayOf(*columns)
    var limit: Int = -1
    var orderBy: MutableList<String> = mutableListOf()

    init {
        if(isDistinct) {
            require(columns.isNotEmpty()) { "Must supply at least one column when using distinct" }
        }
    }

    /**
     * Execute this query on its internal database
     */
    @Throws(SQLException::class)
    fun execute(consumer: ResultSetHandler) {
        database!!.execute(this, consumer)
    }

    /**
     * Execute this query on its internal database
     */
    @Throws(SQLException::class)
    @JvmSynthetic
    fun execute(consumer: (ResultSet) -> Unit) {
        database!!.execute(this, consumer)
    }

    @Deprecated("You should not use run on a query. Use execute instead",
            replaceWith = ReplaceWith("execute { }"),
            level = DeprecationLevel.ERROR)
    override fun run(): Long {
        throw IllegalStateException("You should not use run on a query. Use execute instead")
    }

    /**
     * Set whether this query should return distinct values
     * @return this query
     */
    fun distinct(): Select {
        require(columns.isNotEmpty()) { "Must supply at least one column when using distinct" }
        isDistinct = true
        return this
    }

    /**
     * Limit the number of rows returned by this query
     * @param numRows The number of rows desired
     * @return An instance of this query
     */
    fun limit(numRows: Int): Select {
        limit = numRows
        return this
    }

    /**
     * Add an order by clause
     * @param orderBy SQL order by clause
     * @return An instance of this query
     */
    fun orderBy(@Language("SQL") orderBy: String): Select {
        this.orderBy.add(orderBy)
        return this
    }

    override fun format(): String {
        return database!!.formatSelect(this)
    }
}