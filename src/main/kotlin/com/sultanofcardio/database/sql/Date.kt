package com.sultanofcardio.database.sql

/**
 * Used to map a field in a class to a database date field.
 * This is automatically detected, but uses the date format yyyy-MM-dd hh:mm:ss by default.
 * You may use a custom format by setting the value of this annotation
 */
@Retention(value = AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class Date(
        /**
         * The date DEFAULT_FORMAT to use for date parsing
         * @return The value of the date DEFAULT_FORMAT
         */
        val value: String = DEFAULT_DATE_FORMAT
) {
        companion object {
                const val DEFAULT_DATE_FORMAT = "yyyy-MM-dd hh:mm:ss"
        }
}
