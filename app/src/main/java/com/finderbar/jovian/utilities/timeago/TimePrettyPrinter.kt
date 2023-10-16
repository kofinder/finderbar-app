
package com.finderbar.jovian.utilities.timeago

internal interface TimePrettyPrinter {

    fun print(referenceTime: Long): String
}

val SECOND_IN_MILLS = 1000
val MINITE_IN_MILLS = SECOND_IN_MILLS * 60
val HOUR_IN_MILLS = MINITE_IN_MILLS * 60
