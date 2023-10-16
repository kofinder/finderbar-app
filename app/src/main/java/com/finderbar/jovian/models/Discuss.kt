package com.finderbar.jovian.models

import java.io.Serializable
import java.util.ArrayList


/**
 * Created by thein on 12/19/18.
 */

data class Discuss(
    val _id: String = "",
    val userId: String = "",
    val titleText: String = "",
    val body: String = "",
    val tagIds: List<String>? = ArrayList(),
    val userAvatar: String = "",
    val userName: String = "",
    val answerCount: Int = 0,
    val upVoteCount: Int = 0,
    val downVoteCount: Int = 0,
    val commentCount: Int = 0,
    val viewCount: Int = 0,
    val upVoteHelper: Int = 0,
    val downVoteHelper: Int = 0,
    val favoriteHelper: Int = 0,
    val accepted: Boolean = false,
    val discussType: Int = 0,
    val createdAt: String = "",
    val updatedAt: String = ""
): Serializable

data class DiscussResult(
    val discuss: List<Discuss>,
    val hasNext: Boolean,
    val totalCount: Long
)