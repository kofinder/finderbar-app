package com.finderbar.jovian.utilities.datetime

import org.joda.time.DateTime
import org.joda.time.LocalDate
import java.util.Locale

fun LocalDate.toBuddistString(
    pattern: String = "d MMM yyyy",
    locale: Locale = myanmar
) = this.plusYears(543).toString(pattern, locale)

fun DateTime.toBuddistString(
    pattern: String = "d MMM yyyy HH:mm น.",
    locale: Locale = myanmar
) = this.plusYears(543).toString(pattern, locale)
