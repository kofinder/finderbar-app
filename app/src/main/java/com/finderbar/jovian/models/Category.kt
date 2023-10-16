package com.finderbar.jovian.models

import java.io.Serializable

data class Category(
    var id: String = "",
    var categoryId: String = "",
    var userId: String = "",
    var langPhoto: String = "",
    var languageName: String = "",
    var categoryName: String = "",
    var authorName: String = "",
    var authorAvatar: String = "",
    var articles: Int = 0,
    var createdAt: String = "",
    var updatedAt: String = ""
) : Serializable

data class CategorySearchResult(
    val movies: List<Category>,
    val hasNext: Boolean,
    val totalCount: Long
)

data class CategorySearchQuery(
    val keywords: String = "",
    val excludedKeywords: String = ""
)
