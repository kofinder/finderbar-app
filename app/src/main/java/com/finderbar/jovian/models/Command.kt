package com.finderbar.jovian.models

data class Command(
    val id: String = "",
    val status: String = "",
    val modifyFlag: Int = 0,
    val statusCode: Int = 0,
    val message: String = ""
)
