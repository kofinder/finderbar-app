package com.finderbar.jovian.models

data class Job(
    val _id: String = "",
    val title: String = "",
    val description: String = "",
    val salary: Long = 0,
    val place: String = "",
    val currency: String = "",
    val industryType: String = "",
    val category: String = "",
    val companyName: String = "",
    val companyLogo: String = "",
    val upVoteCount: Long = 0,
    val downVoteCount: Long = 0,
    val commentCount: Long = 0,
    val viewCount: Long = 0,
    val createdAt: String = ""
)

data class JobSearchResult(
    val jobs: List<Job>,
    val hasNext: Boolean,
    val totalCount: Long
)

data class JobSearchQuery(
    val keywords: String = "",
    val excludedKeywords: String = ""
)