package com.finderbar.jovian.models
import type.*
import type.VoteStatus


enum class DiscussType { QUESTION, ANSWER }
enum class VoteType { DOWN, UP, FAV }

data class InputCriteria(
    val _id: String = "",
    val word: String = "",
    val limit: Int = 10,
    val skip: Int = 0) {
    fun get(): Criteria = Criteria.builder()._id(_id).word(word).limit(limit).skip(skip).build()
}

data class InputQuestion(
    val title: String = "",
    val body: String = "",
    val tags: ArrayList<String>) {
    fun get(): QuestionInput = QuestionInput.builder().title(title).body(body).tags(tags).build()
}

data class InputAnswer(
    val questionId: String = "",
    val body: String = "") {
    fun get(): AnswerInput = AnswerInput.builder().questionId(questionId).body(body).build();
}

data class InputComment (
        val userId: String = "",
       val body: String = "" ){
    fun get(): CommentInput = CommentInput.builder().body(body).userId(userId).build();
}

data class InputVote(
        val userId: String = "",
        val status: VoteStatus) {
    fun get(): VoteInput = VoteInput.builder().userId(userId).status(status).build();
}


data class InputUserProfile(
    val userId: String = "",
    val fullName: String = "",
    val avatar: String = "",
    val gender: String = "",
    val relationship: String = "",
    val birthday: String = "",
    val language: String = "",
    val nationality: String = "",
    val facebook: String = "",
    val workPhone: String = "",
    val handPhone: String = "",
    val address: String = "") {
    fun get(): UserProfileInput = UserProfileInput.builder()
            .userId(userId)
            .fullName(fullName)
            .avatar(avatar)
            .gender(gender)
            .relationship(relationship)
            .language(language)
            .nationality(nationality)
            .facebook(facebook)
            .workPhone(workPhone)
            .handPhone(handPhone)
            .address(address).build()
}