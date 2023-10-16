

package com.finderbar.jovian.utilities.timeago

internal class SecondsAgoPrinter(private val currentTimer: CurrentTimer) : TimePrettyPrinter {

    override fun print(referenceTime: Long): String {
        val currentTimeInMills = currentTimer.inMills
        val diff = currentTimeInMills - referenceTime
        val sec = diff / SECOND_IN_MILLS
        return if (sec > 30) {
            "${diff / SECOND_IN_MILLS} s"
        } else {
            "a"
        }
    }
}
