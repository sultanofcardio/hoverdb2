package com.sultanofcardio.database

import com.sultanofcardio.database.sql.statement.Statement
import java.util.*

fun StringBuilder.appendConditions(conditions: Map<String, Any>) {
    val keyIterator = conditions.keys.iterator()
    while (keyIterator.hasNext()) {
        val condition = keyIterator.next()
        val value = conditions[condition]
        var formatString = "%s = "
        formatString += if (value == null) "%s" else if (String::class.java.isAssignableFrom(value.javaClass) ||
                Date::class.java.isAssignableFrom(value.javaClass)) {
            "'%s'"
        } else {
            "%s"
        }
        if (keyIterator.hasNext()) {
            append(String.format(String.format("%s AND ", formatString), condition, value.escape()))
        } else {
            append(String.format(String.format("%s ", formatString), condition, value.escape()))
        }
    }
}

fun StringBuilder.appendConditions(conditions: List<String>) {
    val valueIterator = conditions.iterator()
    while (valueIterator.hasNext()) {
        val condition = valueIterator.next()
        if (valueIterator.hasNext()) {
            append(String.format("%s AND ", condition))
        } else {
            append(String.format("%s ", condition))
        }
    }
}

fun StringBuilder.appendGenericConditions(query: Statement<*>, appendWhere: Boolean) {
    val genericConditions: List<String> = query.genericConditions
    if (genericConditions.isNotEmpty()) {
        if (appendWhere) append("WHERE ") else append("AND ")
    }
    for (i in genericConditions.indices) {
        val condition = genericConditions[i]
        if (i != 0) {
            append("AND ")
        }
        if (i != genericConditions.size - 1) {
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
        appendConditions(s.whereConditions)
    }

    if(s.stringWhereConditions.isNotEmpty()){
        if(!whereAppended) {
            append("WHERE ")
            whereAppended = true
        } else append("AND ")

        appendConditions(s.stringWhereConditions)
    }

    appendGenericConditions(s, !whereAppended)
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