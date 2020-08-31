package com.sultanofcardio.database

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

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

infix fun Any.derivesFrom(type: KClass<*>): Boolean = this::class.isSubclassOf(type)
