
package com.finderbar.jovian.utilities.timeago

internal class HoursAgoPrinter(private val currentTimer: CurrentTimer) : TimePrettyPrinter {

    override fun print(referenceTime: Long): String {
        val currentTimeInMills = currentTimer.inMills
        val diff = currentTimeInMills - referenceTime

        return "${diff / HOUR_IN_MILLS } ชั่วโมงที่แล้ว"
    }
}
