package com.sultanofcardio.database

import com.sultanofcardio.database.sql.statement.Statement
import java.util.*

fun StringBuilder.appendWhereConditions(s: Statement<*>) {
    val keyIterator = s.whereConditions.keys.iterator()
    while (keyIterator.hasNext()) {
        val condition = keyIterator.next()
        val value = s.whereConditions[condition]
        var formatString = "%s = "
        formatString += if (value == null) "%s" else if (String::class.java.isAssignableFrom(value.javaClass) ||
                Date::class.java.isAssignableFrom(value.javaClass)) {
            "'%s'"
        } else {
            "%s"
        }
        if (keyIterator.hasNext()) {
            append(String.format(String.format("%s AND ", formatString), condition, s.escapeValue(value)))
        } else {
            append(String.format(String.format("%s ", formatString), condition, s.escapeValue(value)))
        }
    }
}

fun StringBuilder.appendStringWhereConditions(s: Statement<*>) {
    val valueIterator = s.stringWhereConditions.iterator()
    while (valueIterator.hasNext()) {
        val condition = valueIterator.next()
        if (valueIterator.hasNext()) {
            append(String.format("%s AND ", condition))
        } else {
            append(String.format("%s ", condition))
        }
    }
}

fun StringBuilder.appendAllConditions(s: Statement<*>) {
    var whereAppended = false

    if(s.whereConditions.isNotEmpty()) {
        append("WHERE ")
        whereAppended = true
        appendWhereConditions(s)
    }

    if(s.stringWhereConditions.isNotEmpty()){
        if(!whereAppended) {
            append("WHERE ")
        } else append("AND ")

        appendStringWhereConditions(s)
    }
}

fun properties(receiver: Properties.() -> Unit): Properties {
    val props = Properties()
    receiver(props)
    return props
}

fun properties(vararg properties: Pair<Any, Any>): Properties {
    val props = Properties()
    props.putAll(properties)
    return props
}