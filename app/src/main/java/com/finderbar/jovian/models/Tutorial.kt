package com.finderbar.jovian.models

import java.io.Serializable

data class Tutorial(
    var id: String = "",
    var titleText: String = "",
    var htmlBody: String = "",
    var serializedBody: String = "",
    var commentCount: Int = 0,
    var viewCount: Int = 0,
    var createdAt: String = "",
    var updatedAt: String = ""
):Serializable