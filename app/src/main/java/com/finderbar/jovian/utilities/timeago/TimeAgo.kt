
package com.finderbar.jovian.utilities.timeago

import org.joda.time.DateTime

fun DateTime.toTimeAgo() = TimePrettyPrinterFactory(JodaCurrentTime()).print(millis)
