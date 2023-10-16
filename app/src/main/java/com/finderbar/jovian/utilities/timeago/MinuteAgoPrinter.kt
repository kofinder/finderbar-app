package com.finderbar.jovian.utilities.timeago

internal class MinuteAgoPrinter(private val currentTimer: CurrentTimer) : TimePrettyPrinter {

    override fun print(referenceTime: Long): String {
        val currentTimeInMills = currentTimer.inMills
        val diff = currentTimeInMills - referenceTime
        return "${diff / MINITE_IN_MILLS} นาทีที่แล้ว"
    }
}
