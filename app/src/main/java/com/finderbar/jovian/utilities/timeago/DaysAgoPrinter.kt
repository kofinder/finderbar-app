
package com.finderbar.jovian.utilities.timeago

import org.joda.time.DateTime

import java.util.Locale

internal class DaysAgoPrinter(private val currentTimer: CurrentTimer) : TimePrettyPrinter {

    override fun print(referenceTime: Long): String {
        val currentTimeInMills = DateTime(currentTimer.inMills)
        val agoDateTime = DateTime(referenceTime)

        val diffDay = currentTimeInMills.dayOfYear - agoDateTime.dayOfYear
        if (diffDay == 1) {
            val dateTime = DateTime(referenceTime)
            return String.format(Locale.US, "เมื่อวาน %02d:%02d", dateTime.getHourOfDay(), dateTime.getMinuteOfHour())
        } else {

            return "$diffDay วันที่แล้ว"
    }
    }
}
