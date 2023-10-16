package com.finderbar.jovian.utilities

import com.apollographql.apollo.response.CustomTypeAdapter
import com.apollographql.apollo.response.CustomTypeValue
import java.text.SimpleDateFormat
import java.util.*

class CustomDateAdapter : CustomTypeAdapter<Date> {

    companion object {
        const val DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    }

    override fun encode(value: Date): CustomTypeValue<*> {
        // Parse Date in UTC TimeZone
        val calendar = Calendar.getInstance()
        calendar.timeZone = TimeZone.getTimeZone("UTC")
        calendar.time = value
        val time = calendar.time

        // Parse Date in UTC Format
        val sdf = SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH)
        return CustomTypeValue.GraphQLString(sdf.format(time))
    }

    override fun decode(value: CustomTypeValue<*>): Date {
        // Parse UTC formatted Date String in Date
        val sdf = SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val date = sdf.parse(value.value.toString())

        // Change timezone to default
        val calendar = Calendar.getInstance()
        calendar.timeZone = TimeZone.getDefault()
        calendar.time = date

        return calendar.time
    }
}
