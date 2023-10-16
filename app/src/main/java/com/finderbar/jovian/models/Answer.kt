package com.finderbar.jovian.models
import java.io.Serializable

data class Answer(
    val _id: String = "",
    var userId: String = "",
    var questionId: String = "",
    val body: String = "",
    val commentCount: Long = 0,
    val upVoteCount: Long = 0,
    val downVoteCount: Long = 0,
    var userName: String = "",
    var userAvatar: String = "",
    var createdAt: String = "",
    var updateAt: String = ""
): Serializable

data class AnswerResult(
    val answers: List<Answer>,
    val hasNext: Boolean,
    val totalCount: Long
)