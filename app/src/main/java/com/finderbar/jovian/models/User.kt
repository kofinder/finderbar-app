package com.finderbar.jovian.models

/**
 * Created by thein on 1/12/19.
 */
data class User(
    val _id: String = "",
    val userName: String = "",
    val avatar: String = "",
    val gender: String = "",
    val online: Boolean = false,
    val createdAt: String = ""
)

data class UserSearchResult(
    val users: List<User>,
    val hasNext: Boolean,
    val totalCount: Long
)

data class UserSearchQuery(
    val keywords: String = "",
    val excludedKeywords: String = ""
)
