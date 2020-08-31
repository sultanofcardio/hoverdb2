package com.sultanofcardio.database.sql.statement

import org.intellij.lang.annotations.Language

/**
 * Class representing an instance of an SQL update query
 * @param <T> Optional type parameter of your subclass
</T> */
class Update (tableName: String) : Statement<Update>(Type.DML) {
    val setConditions: MutableMap<String, Any> = mutableMapOf()
    val stringSetConditions: MutableList<String> = mutableListOf()

    init {
        this.tableName = tableName
    }


    /**
     * Specify a column name-value pair to be injected into this update statement
     * @param column The name of the column to be set
     * @param value The value to set to the named column
     * @return this update statement
     */
    operator fun set(column: String, value: Any): Update {
        setConditions[column.escape()] = value
        return this
    }

    /**
     * Specify several pairs of column names and values for injecting into the update statement
     * @param values pairs of column names and values
     * @return this update statement
     */
    fun set(values: Map<String, Any>): Update {
        values.forEach { (key, value) ->
            setConditions[key.escape()] = value
        }
        return this
    }

    /**
     * Specify a raw SQL set expression, of the form `SET column = value`.
     * Do not include any commas in this expression
     * @param condition valid SQL set expression
     * @return this update statement
     */
    fun set(@Language("SQL") condition: String): Update {
        stringSetConditions.add(condition)
        return this
    }

    override fun format(): String {
        return database!!.formatUpdate(this)
    }
}