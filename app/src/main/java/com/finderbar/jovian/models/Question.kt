package com.finderbar.jovian.models
import java.io.Serializable
import java.util.ArrayList

data class Question(
    val _id: String = "",
    val titleText: String = "",
    val tagIds: List<String>? = ArrayList(),
    val answerCount: Int = 0,
    val upVoteCount: Int = 0,
    val downVoteCount: Int = 0,
    val commentCount: Int = 0,
    val viewCount: Int = 0,
    val userAvatar: String = "",
    val userName: String = "",
    val createdAt: String = ""
): Serializable


data class QuestionSearchResult(
    val questions:  List<Question>,
    val hasNext: Boolean,
    val totalCount: Long) {

}

data class QuestionSearchQuery(
    val keywords: String = "",
    val excludedKeywords: String = ""
)



