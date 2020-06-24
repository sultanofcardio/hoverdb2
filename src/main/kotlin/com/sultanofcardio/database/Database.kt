@file:Suppress("MemberVisibilityCanBePrivate")

package com.sultanofcardio.database

import com.sultanofcardio.database.interfaces.ConnectionHandler
import com.sultanofcardio.database.interfaces.DatabaseHandler
import com.sultanofcardio.database.interfaces.ResultSetHandler
import com.sultanofcardio.database.sql.statement.*
import org.intellij.lang.annotations.Language
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException

interface Database<T: Database<T>> {

    val driverName: String

    /**
     * Execute a raw SQL query that auto-closes its resources
     * @param sql Valid SQL code
     * @param resultSetHandler Handle the result of the query
     * @see run(String)
     */
    @Throws(SQLException::class)
    @JvmDefault
    fun execute(@Language("SQL") sql: String, resultSetHandler: ResultSetHandler) = execute(sql) { resultSetHandler.handle(it) }

    /**
     * Execute a raw SQL query that auto-closes its resources
     * @param sql Valid SQL code
     * @param resultSetHandler Handle the result of the query
     * @see run(String)
     */
    @Throws(SQLException::class)
    @JvmSynthetic
    fun execute(@Language("SQL") sql: String, resultSetHandler: (ResultSet) -> Unit) {
        connection {  connection ->
            connection.prepareStatement(sql) { statement ->
                statement.executeQuery()!!.use {
                    resultSetHandler(it)
                }
            }
        }
    }

    /**
     * Execute a query object. The result set will only be valid inside the context of the handler
     * @param query A query object that formats to valid SQL code
     * @param resultSetHandler Handle the result of the query
     * @see run(String)
     */
    @Throws(SQLException::class)
    @JvmDefault
    fun execute(query: Statement<*>, resultSetHandler: ResultSetHandler) = execute(query) { resultSetHandler.handle(it) }

    /**
     * Execute a query object. The result set will only be valid inside the context of the handler
     * @param query A query object that formats to valid SQL code
     * @param resultSetHandler Handle the result of the query
     * @see run(String)
     */
    @Throws(SQLException::class)
    @JvmSynthetic
    fun execute(query: Statement<*>, resultSetHandler: (ResultSet) -> Unit) {
        execute(query.toString(), resultSetHandler)
    }

    /**
     * Get a short-lived database connection. The connection must be used within the scope of the consumer and will
     * be auto-closed
     */
    @Throws(SQLException::class)
    @JvmSynthetic
    fun <R> connection(consumer: (Connection) -> R): R = getConnection(getJDBCUrl()).use { consumer(it) }

    /**
     * Get a short-lived database connection. The connection must be used within the scope of the consumer and will
     * be auto-closed
     */
    @Throws(SQLException::class)
    @JvmDefault
    fun <R> connection(consumer: ConnectionHandler<R>): R = connection { consumer.handle(it) }

    /**
     * Run an auto-committed database transaction on a [Database] object that will be rolled back if anything goes
     * wrong
     */
    @JvmSynthetic
    fun <R> transaction(consumer: Database<T>.() -> R): R {
        return connection { connection ->
            connection.autoCommit = false
            try {
                @Suppress("UNCHECKED_CAST") val t: Database<T> = object: Database<T> {

                    override val driverName: String = this@Database.driverName

                    override fun execute(sql: String, resultSetHandler: ResultSetHandler) {
                        connection.prepareStatement(sql) { statement ->
                            statement.executeQuery()!!.use {
                                resultSetHandler.handle(it)
                            }
                        }
                    }

                    override fun run(sql: String): Long = connection.prepareStatement(sql) { statement -> statement.executeUpdate().toLong() }

                    override fun getConnection(jdbcUrl: String): Connection = this@Database.getConnection(jdbcUrl)

                    override fun getJDBCUrl(): String = this@Database.getJDBCUrl()

                    override fun formatSelect(select: Select): String = this@Database.formatSelect(select)

                    override fun formatUpdate(update: Update): String = this@Database.formatUpdate(update)

                    override fun formatInsert(insert: Insert): String = this@Database.formatInsert(insert)

                    override fun formatDelete(delete: Delete): String = this@Database.formatDelete(delete)

                }
                val r = consumer(t)
                connection.commit()
                r
            } catch (e: Throwable) {
                connection.rollback()
                throw e
            }
        }
    }

    /**
     * Run an auto-committed database transaction on a [Database] object that will be rolled back if anything goes
     * wrong
     */
    @JvmDefault
    fun <R> transaction(consumer: DatabaseHandler<Database<*>, R>): R {
        return transaction { consumer.handle(this) }
    }

    /**
     * Run a raw SQL statement that modifies the database.
     * @param sql Valid SQL code
     * @return The number of rows affected by the query
     * @see execute(String)
     */
    @Throws(SQLException::class)
    @JvmDefault
    fun run(@Language("SQL") sql: String): Long {
        return connection {  connection ->
            connection.prepareStatement(sql) {statement -> statement.executeUpdate().toLong() }
        }
    }

    /**
     * Run a raw SQL query that modifies the database
     * @param statement A statement object that formats to valid SQL code
     * @return The number of rows affected by the query
     * @see execute(String)
     */
    @Throws(SQLException::class)
    @JvmDefault
    fun run(statement: Statement<*>): Long {
        return run(statement.toString())
    }

    /**
     * Initiate a new select query
     * @param columns The columns to select
     * @return The select query instance
     */
    @JvmDefault
    fun select(vararg columns: String): Select {
        return select(false, *columns).let{ it.database = this; it }
    }

    /**
     * Initiate a new select query, specifying whether or not to have distinct results
     * @param columns The columns to select
     * @param distinct Specify whether or not to have distinct results
     * @return The select query instance
     */
    @JvmDefault
    fun select(distinct: Boolean, vararg columns: String): Select {
        return Select(distinct, *columns).let{ it.database = this; it }
    }

    /**
     * Initiate a new insert query
     * @return The insert query instance
     */
    @JvmDefault
    fun insert(): Insert {
        return Insert().let{ it.database = this; it }
    }

    /**
     * Initiate a new delete query
     * @return The delete query instance
     */
    @JvmDefault
    fun delete(): Delete {
        return Delete().let{ it.database = this; it }
    }

    /**
     * Initiate a new update query
     * @param tableName The name of the table to update
     * @return The update query instance
     */
    @JvmDefault
    fun update(tableName: String): Update {
        return Update(tableName).let{ it.database = this; it }
    }

    /**
     * Produce a valid [Connection] object for your RDBMS
     */
    fun getConnection(jdbcUrl: String): Connection

    /**
     * Format your JDBC URL
     */
    fun getJDBCUrl(): String

    /**
     * Format a valid select query for your RDBMS
     * @param select the select query
     * @return The formatted query
     */
    fun formatSelect(select: Select): String

    /**
     * Format a valid update query for your RDBMS
     * @param update the update query
     * @return The formatted query
     */
    fun formatUpdate(update: Update): String

    /**
     * Format a valid insert query for your RDBMS
     * @param insert the insert query
     * @return The formatted query
     */
    fun formatInsert(insert: Insert): String

    /**
     * Format a valid delete query for your RDBMS
     * @param delete the delete query
     * @return The formatted query
     */
    fun formatDelete(delete: Delete): String
}