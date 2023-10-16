package com.finderbar.jovian.models

import java.io.Serializable

data class RemoteNotify(
    val notifyLinkId: String = "",
    val badgeCount: Int = 0,
    val contentTitle: String = "FinderBar",
    val contentText: String = "you have something change"
): Serializable

data class Notification(
    val _id: String = "",
    val notifyId: String = "",
    val title: String = "",
    val body: String = "",
    val userName: String = "",
    val userAvatar: String = "",
    val createdAt: String = "",
    val updatedAt: String = ""
)

data class NotificationResult(
    val notiList: List<Notification>,
    val hasNext: Boolean,
    val totalCount: Long
)