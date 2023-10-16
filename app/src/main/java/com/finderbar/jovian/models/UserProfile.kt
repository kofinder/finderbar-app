package com.finderbar.jovian.models

import java.io.Serializable

data class Experience(
    val expId: String? = "",
    val position: String? = "",
    val company: String? = "",
    val fromDate: String? = "",
    val toDate: String? = "",
    val expDescription: String? = ""
)

data class Education(
    val eduId: String? = "",
    val school: String? = "",
    val city: String? = "",
    val fromDate: String? = "",
    val toDate: String? = "",
    val eduDescription: String? = ""
)

data class Skill(
    val skillId: String? = "",
    val language: String? = "",
    val framework: String? = "",
    val database: String? = ""
)

data class Hobbie(
    val hobbId: String? = "",
    val travel: String? = "",
    val movie: String? = "",
    val music: String? = "",
    val game: String? = "",
    val book: String? = ""
)


data class UserProfile(
    val _id: String? = "",
    val userName: String? = "",
    val avatar: String? = "",
    val gender: String? = "",
    val relationship: String? = "",
    val birthday: String? = "",
    val language: String? = "",
    val nationality: String? = "",
    val facebook: String? = "",
    val workPhone: String? = "",
    val handPhone: String? = "",
    val address: String? = "",
    val profileViewCount : Long? = 0,
    val answerCount: Long? = 0,
    val questionCount: Long? = 0,
    val badgeCount: BadgeCounts? = null,
    val repetition: Long? = 0,
    val experiences: MutableList<Experience> = ArrayList(),
    val educations: MutableList<Education> = ArrayList(),
    val skills: MutableList<Skill> = ArrayList(),
    val hobbies: MutableList<Hobbie> = ArrayList()
): Serializable