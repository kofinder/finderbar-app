package com.finderbar.jovian.utilities.android

import android.content.Context
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

@Throws(IOException::class)
fun Context.assetAsString(filename: String): String {
    val reader = BufferedReader(InputStreamReader(assets.open(filename)))
    return reader.readText()
}
