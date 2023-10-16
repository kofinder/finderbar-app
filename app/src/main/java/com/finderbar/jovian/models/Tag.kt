package com.finderbar.jovian.models

/**
 * Created by thein on 1/12/19.
 */
data class Tags(
    val tagName: String = "",
    val tagCount: String = ""
)

data class TagSearchResult(
    val tagList: List<Tags>,
    val hasNext: Boolean,
    val totalCount: Long
)

data class TagSearchQuery(
    val keywords: String = "",
    val excludedKeywords: String = ""
)