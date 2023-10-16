
package com.finderbar.jovian.utilities.timeago

import org.joda.time.DateTime

internal class JodaCurrentTime : CurrentTimer {
    override val inMills: Long
        get() = DateTime.now().getMillis()
}
