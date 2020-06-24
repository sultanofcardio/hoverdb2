package com.sultanofcardio.database

import com.sultanofcardio.database.sql.Date
import com.sultanofcardio.database.sql.Literal
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.text.SimpleDateFormat
import kotlin.reflect.KClass

/**
 * Transform a ResultSet into an immutable list of objects
 */
fun <R> ResultSet.list(mapper: (ResultSet) -> R): List<R> {
    val list = mutableListOf<R>()
    while(next()) list.add(mapper(this))
    return list
}

/**
 * Transform a ResultSet into an immutable set of objects
 */
fun <R> ResultSet.set(mapper: (ResultSet) -> R): Set<R> {
    val set = mutableSetOf<R>()
    while(next()) set.add(mapper(this))
    return set
}

/**
 * Prepare an auto-closing statement, valid only within the context of the handler
 */
fun <T> Connection.prepareStatement(sql: String, handler: (PreparedStatement) -> T): T {
    val statement: PreparedStatement = prepareStatement(sql)
    val t = handler(statement)
    if(!statement.isClosed) try { statement.close() } catch(ignored: Throwable) {}
    return t
}

/**
 * Escapes any &#39; characters in the string representation of the input
 * @param value The input to be escaped
 * @return The escaped value
 */
fun Any?.escape(): String {
    var sValue = this.toString()
    if (this is Literal) return sValue
    if (this != null && java.util.Date::class.java.isAssignableFrom(this@escape.javaClass))
        sValue = (this as java.util.Date).formatDate()
    return sValue.replace("'", "''")
}

fun java.util.Date.formatDate(): String {
    var dateFormat: String = Date.DEFAULT_DATE_FORMAT
    if (this@formatDate.javaClass.getDeclaredAnnotation(Date::class.java) != null) {
        dateFormat = this@formatDate.javaClass.getDeclaredAnnotation(Date::class.java).value
    }
    return SimpleDateFormat(dateFormat).format(this)
}

infix fun Any.derivesFrom(type: KClass<*>): Boolean = this::class.java.isAssignableFrom(type.java)