package com.finderbar.jovian.datasource

import com.finderbar.jovian.models.*
import query.*


fun convertUser(result: MutableList<AllUsersQuery.User>): List<User> {
    var list = ArrayList<User>()
    result.forEach { list.add(User(it._id(), it.userName(), it.avatar()!!, it.gender()!!, it.online()!!)) }
    return list
}

fun convertQuestionDownVoteUser(result: MutableList<AllUserByQuestionDownVoteQuery.User>) : ArrayList<User> {
    var list = ArrayList<User>()
    result.forEach { list.add(User(it._id(), it.userName(), it.avatar()!!, it.gender()!!, it.online()!!, it.createdAt()!!)) }
    return list
}

fun convertQuestionUpVoteUser(result: MutableList<AllUserByQuestionUpVoteQuery.User>) : ArrayList<User> {
    var list = ArrayList<User>()
    result.forEach { list.add(User(it._id(), it.userName(), it.avatar()!!, it.gender()!!, it.online()!!, it.createdAt()!!)) }
    return list
}

fun convertAnswerUpVoteUser(result: MutableList<AllUserByAnswerUpVoteQuery.User>) : ArrayList<User> {
    var list = ArrayList<User>()
    result.forEach { list.add(User(it._id(), it.userName(), it.avatar()!!, it.gender()!!, it.online()!!, it.createdAt()!!)) }
    return list
}

fun convertAnswerDownVoteUser(result: MutableList<AllUserByAnswerDownVoteQuery.User>) : ArrayList<User> {
    var list = ArrayList<User>()
    result.forEach { list.add(User(it._id(), it.userName(), it.avatar()!!, it.gender()!!, it.online()!!, it.createdAt()!!)) }
    return list
}

fun convertAnswer(result: MutableList<AllAnswerByQuestionIdQuery.Answer>) : ArrayList<Answer> {
    var list = ArrayList<Answer>()
    result.forEach {list.add(Answer( it._id(),
            it.userId(),
            it.questionId(),
            it.body(),
            it.commentCount().toLong(),
            it.upVoteCount().toLong(),
            it.downVoteCount().toLong(),
            it.userName(),
            it.userAvatar()!!,
            it.createdAt()
    ))}

    return list
}

fun convertDiscuss(result: MutableList<AllDiscussQuery.Discuss>) : ArrayList<Discuss> {
    var list = ArrayList<Discuss>()
    result.forEach {list.add(Discuss(
            it._id(),
            it.userId(),
            it.title()?.toString() ?: "",
            it.body(),
            it.tags(),
            it.userAvatar()?.toString() ?: "",
            it.userName(),
            it.answerCount()?.toInt() ?: 0,
            it.upVoteCount(),
            it.downVoteCount(),
            it.commentCount(),
            it.viewCount()?.toInt() ?: 0,
            it.upVoteHelper(),
            it.downVoteHelper(),
            it.favoriteHelper(),
            it.accepted()?.or(false) ?: true,
            it.discussType(),
            it.createdAt()
    ))}

    return list
}


fun convertQuestions(result: MutableList<AllQuestionsQuery.Question>): List<Question> {
    val list = ArrayList<Question>()
    result.forEach {list.add(Question(it._id(), it.title(), it.tags(), it.answerCount(),
            it.upVoteCount(), it.downVoteCount(), it.commentCount(), it.viewCount(),
            it.userAvatar()!!, it.userName(), it.createdAt()))}
    return list;
}

fun convertUserQuestions(result: MutableList<AllQuestionByUserQuery.Question>): List<Question> {
    val list = ArrayList<Question>()
    result.forEach {list.add(Question(it._id(), it.title(), it.tags(), it.answerCount(),
            it.upVoteCount(), it.downVoteCount(), it.commentCount(), it.viewCount(),
            it.userAvatar()!!, it.userName(), it.createdAt()))}
    return list;
}

fun convertUserAnswer(result: MutableList<AllAnswerByUserQuery.Answer>) : ArrayList<Answer> {
    var list = ArrayList<Answer>()
    result.forEach {list.add(Answer(
            it._id(),
            it.userId(),
            it.questionId(),
            it.body(),
            it.commentCount().toLong(),
            it.upVoteCount().toLong(),
            it.downVoteCount().toLong(),
            it.userName(),
            it.userAvatar()!!,
            it.createdAt()
    ))}

    return list
}



fun convertCategory(result: MutableList<AllCategoriesQuery.Category>): List<Category> {
    var list = ArrayList<Category>()
    result.forEach { list.add(Category(
            it._id(),
            it.categoryId(),
            it.userId(),
            it.langPhoto(),
            it.languageName(),
            it.categoryName(),
            it.authorName(),
            it.authorAvatar(),
            it.articles(),
            it.createdAt()
    ))}

    return list
}

fun getResultList(result: MutableList<AllJobsQuery.Job>): List<Job> {
    var list = ArrayList<Job>()
    result.forEach { list.add(Job(
            it._id(),
            it.title(),
            it.description(),
            it.salary().toLong(),
            it.place(),
            it.currency(),
            it.industryType(),
            it.category(),
            "finderbar",
            "https://finderresources.s3-ap-southeast-1.amazonaws.com/bbOZFz91_400x400.jpg",
            0,
            0,
            it.commentCount().toLong(),
            it.viewCount().toLong(),
            it.createdAt()
    ))}

    return list
}

fun convertComment(result: MutableList<AllCommentByQuestionQuery.Comment>?): List<Comment> {
    val list = ArrayList<Comment>()
    result?.forEach{
        list.add(Comment(
                it._id(),
                it.body(),
                it.userId(),
                it.userName(),
                it.userAvatar(),
                it.createdAt()
        ))
    }
    return list
}

fun convertAnswerComment(result: MutableList<AllCommentByAnswerQuery.Comment>?): List<Comment> {
    val list = ArrayList<Comment>()
    result?.forEach{
        list.add(Comment(
                it._id(),
                it.body(),
                it.userId(),
                it.userName(),
                it.userAvatar(),
                it.createdAt()
        ))
    }
    return list
}