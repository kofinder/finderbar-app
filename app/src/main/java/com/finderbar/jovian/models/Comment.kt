package com.finderbar.jovian.models

/**
 * Created by thein on 12/22/18.
 */
data class Comment(
    val _id: String = "",
    val body: String = "",
    val userId: String = "",
    val userName: String = "",
    val userAvatar: String = "",
    val createdAt: String = ""
)

data class CommentResult(
    val comments: List<Comment>,
    val hasNext: Boolean,
    val totalCount: Long
)