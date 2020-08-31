package com.sultanofcardio.database.sql.statement

/**
 * Class representing an instance of an SQL insert query
 */
class Insert : Statement<Insert>(Type.DML) {
    val columnValues: MutableMap<String, Any> = mutableMapOf()

    /**
     * Specify the name of the table to insert into
     * @param tableName The table name
     * @return this insert query
     */
    fun into(tableName: String): Insert {
        this.tableName = tableName
        return this
    }

    /**
     * Specify a pair of column name and value for injecting into the insert statement
     * @param columnName The name of the column to insert into
     * @param value The value to insert into the column
     * @return this insert statement
     */
    fun value(columnName: String, value: Any): Insert {
        columnValues[columnName.escape()] = value
        return this
    }

    /**
     * Specify several pairs of column names and values for injecting into the insert statement
     * @param values pairs of column names and values
     * @return this insert statement
     */
    fun values(values: Map<String, Any>): Insert {
        values.forEach { (key, value) ->
            columnValues[key.escape()] = value
        }
        return this
    }

    /**
     * Format this statement to SQL
     */
    override fun format(): String {
        return database!!.formatInsert(this)
    }
}