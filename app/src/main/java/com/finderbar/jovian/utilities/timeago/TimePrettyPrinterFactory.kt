
package com.finderbar.jovian.utilities.timeago

import org.joda.time.DateTime

internal class TimePrettyPrinterFactory(private val currentTimer: CurrentTimer) : TimePrettyPrinter {

    override fun print(referenceTime: Long): String {
        val reference = DateTime(referenceTime)
        val current = DateTime(currentTimer.inMills)

        val diffMills = current.getMillis() - reference.getMillis()
        return if (diffMills < MINITE_IN_MILLS)
            SecondsAgoPrinter(currentTimer).print(referenceTime)
        else if (diffMills < HOUR_IN_MILLS)
            MinuteAgoPrinter(currentTimer).print(referenceTime)
        else if (current.year == reference.year && current.dayOfYear - reference.dayOfYear == 0)
            HoursAgoPrinter(currentTimer).print(referenceTime)
        else if (current.year == reference.year && current.dayOfYear - reference.dayOfYear == 1)
            DaysAgoPrinter(currentTimer).print(referenceTime)
        else
            DateTimePrinter(currentTimer).print(referenceTime)
    }
}
