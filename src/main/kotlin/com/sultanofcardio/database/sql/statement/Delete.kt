package com.sultanofcardio.database.sql.statement

/**
 * Class representing an instance of an SQL delete query
</T> */
class Delete: Statement<Delete>(Type.DML) {
    override fun format(): String {
        return database!!.formatDelete(this)
    }
}