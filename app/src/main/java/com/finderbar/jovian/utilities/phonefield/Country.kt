package com.finderbar.jovian.utilities.phonefield

import android.content.Context
import java.util.*

/**
 * Created by FINDERBAR on 11/30/18.
 */
class Country(val code: String, val name: String, val dialCode: Int) {

    val displayName: String
        get() = Locale("", code).getDisplayCountry(Locale.US)

    fun getResId(context: Context): Int {
        val name = String.format("country_flag_%s", code.toLowerCase())
        val resources = context.resources
        return resources.getIdentifier(name, "drawable", context.packageName)
    }
}