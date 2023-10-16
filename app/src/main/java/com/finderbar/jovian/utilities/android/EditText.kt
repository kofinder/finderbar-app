package com.finderbar.jovian.utilities.android

import android.widget.EditText

fun EditText.getInput(block: (String) -> Unit) {
    if (!text.isNullOrBlank()) block(text.toString())
}

val EditText.isNotBlank: Boolean
    get() = !text.isNullOrBlank()
