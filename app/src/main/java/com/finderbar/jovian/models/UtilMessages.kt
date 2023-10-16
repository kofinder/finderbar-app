package com.finderbar.jovian.models

/**
 * Created by thein on 1/14/19.
 */
data class ErrorMessage(
    val status: String = "",
    val statusCode: Long = 0,
    val message: String = ""
)

data class ModifyMessage(
    val status: String = "",
    val modifyFlag: Long = 0,
    val statusCode: Long = 0,
    val message: String = ""
)