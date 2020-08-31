@file:Suppress("MemberVisibilityCanBePrivate")

package com.sultanofcardio.database.vendor

import com.sultanofcardio.database.Database
import com.sultanofcardio.database.appendAllConditions
import com.sultanofcardio.database.derivesFrom
import com.sultanofcardio.database.sql.statement.Delete
import com.sultanofcardio.database.sql.statement.Insert
import com.sultanofcardio.database.sql.statement.Select
import com.sultanofcardio.database.sql.statement.Update
import org.sqlite.JDBC
import java.io.File
import java.sql.Connection
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

open class SQLite @JvmOverloads constructor(
        var file: File,
        val properties: Properties = Properties()
): Database<SQLite> {

    override val driverName = "org.sqlite.JDBC"

    override fun getConnection(jdbcUrl: String): Connection = JDBC.createConnection(jdbcUrl, properties)

    override fun getJDBCUrl(): String = "jdbc:sqlite:${file.absolutePath}"

    override fun formatSelect(select: Select): String {
        return buildString {
            if(select.isDistinct) append("SELECT DISTINCT ") else append("SELECT ")
            if(select.columns.isNotEmpty()){
                select.columns.forEachIndexed { i, column ->
                    val comma = if (i != select.columns.lastIndex) "," else ""
                    append("${column.escape()}$comma ")
                }
            } else append("* ")

            append("FROM ${select.tableName} ")

            appendAllConditions(select)

            if(select.orderBy.isNotEmpty()) {
                append("ORDER BY ")
                select.orderBy.forEachIndexed { i, orderBy ->
                    append(orderBy)
                    if(i != select.orderBy.lastIndex) append(", ")
                }
            }

            if(select.limit != -1){
                append(" LIMIT ${select.limit} ")
            }
        }.trim()
    }

    override fun formatUpdate(update: Update): String {
        return buildString {
            append("UPDATE ${update.tableName} SET ")

            check(update.setConditions.isNotEmpty() || update.stringSetConditions.isNotEmpty()) { "No column values found to modify" }

            val keys = update.setConditions.keys.toList()

            keys.forEachIndexed { i, columnName ->
                val value = update.setConditions[columnName]!!
                val a = if(value derivesFrom String::class || value derivesFrom Date::class) "'" else ""

                if(i != keys.lastIndex) {
                    append("$columnName = $a${value.escape()}$a, ")
                } else append("$columnName = $a${value.escape()}$a ")
            }

            if (update.stringSetConditions.isNotEmpty() && update.setConditions.isNotEmpty()) {
                append(", ")
            }

            for (i in update.stringSetConditions.indices) {
                val condition = update.stringSetConditions[i]
                if (i != update.stringSetConditions.size - 1) {
                    append(String.format("%s, ", condition))
                } else {
                    append(String.format("%s ", condition))
                }
            }

            appendAllConditions(update)
        }.trim()
    }

    /**
     * Format an Insert object as a valid raw SQL string for the MySQL database
     * @param insert the insert query
     * @return valid MySQL code
     */
    override fun formatInsert(insert: Insert): String {
        check(insert.columnValues.isNotEmpty()) { "No values found to be inserted" }
        return buildString {
            append("INSERT INTO ${insert.tableName}(")
            val columnNames = insert.columnValues.keys.toList()
            val valuesString = StringBuilder("VALUES(")
            columnNames.forEachIndexed { i, columnName ->
                val value = insert.columnValues[columnName]!!
                val a = if(value derivesFrom String::class || value derivesFrom Date::class) "'" else ""
                if(i != columnNames.lastIndex) {
                    append("$columnName, ")
                    valuesString.append("$a${value.escape()}$a, ")
                } else {
                    append("$columnName) ")
                    valuesString.append("$a${value.escape()}$a)")
                }
            }
            append(valuesString.trim())
        }
    }

    /**
     * Format a Delete object as a valid raw SQL string for the MySQL database
     * @param delete the delete query
     * @return valid MySQL code
     */
    override fun formatDelete(delete: Delete): String {
        return buildString {
            append("DELETE FROM ${delete.tableName} ")
            appendAllConditions(delete)
        }.trim()
    }

    override fun formatDate(date: Date): String = SimpleDateFormat(dateTimeFormat).format(date)

    override fun formatDate(date: LocalDate): String = date.toString()

    override fun formatDate(date: LocalDateTime): String = DateTimeFormatter.ofPattern(dateTimeFormat).format(date)

    override fun formatDate(date: LocalTime): String = DateTimeFormatter.ofPattern(timeFormat).format(date)
}
