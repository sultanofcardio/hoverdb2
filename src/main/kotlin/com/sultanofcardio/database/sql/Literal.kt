package com.sultanofcardio.database.sql

data class Literal(val value: String){
    override fun toString(): String = value
}