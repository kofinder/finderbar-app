package com.finderbar.jovian.viewmodels.discuss

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.apollographql.apollo.ApolloMutationCall
import com.finderbar.jovian.*
import com.finderbar.jovian.models.*
import mutation.*

/**
 * Created by finderbar on 1/3/19.
 */
class AskAnswerVM: ViewModel() {

    var errorMessage: MutableLiveData<ErrorMessage> = MutableLiveData()
    var result: MutableLiveData<Command> = MutableLiveData()

    private var saveQuestion: ApolloMutationCall<SaveQuestionMutation.Data>? = null
    private var editQuestion: ApolloMutationCall<EditQuestionMutation.Data>? = null
    private var saveAnswer: ApolloMutationCall<SaveAnswerMutation.Data>? = null
    private var editAnswer: ApolloMutationCall<EditAnswerMutation.Data>? = null
    private var saveQuestionComment: ApolloMutationCall<SaveQuestionCommentMutation.Data>? = null
    private var saveAnswerComment: ApolloMutationCall<SaveAnswerCommentMutation.Data>? = null

    fun saveQuestion(title: String, body: String, tags: MutableList<String>) {
        saveQuestion?.cancel()
        val question = InputQuestion(title, body, tags as ArrayList<String>)
        saveQuestion = apolloClient.mutate(SaveQuestionMutation.builder().question(question.get()).build());
        saveQuestion?.enqueue({
            val error = it.data()?.createQuestion()?.error();
            if(error != null) {
                errorMessage.postValue(ErrorMessage(error.status()!!, error.statusCode()!!.toLong(), error.message()!!))
            } else {
                val datum = it.data()?.createQuestion()!!.command();
                result.postValue(Command(datum?.id()!!, datum?.status()!!, datum.modifyFlag()!!, datum.statusCode()!!, datum.message()!!))
            }
        }, {errorMessage.postValue(ErrorMessage("fail", 500, it.message!!))})
    }

    fun editQuestion(_id: String, title: String, body: String, tags: MutableList<String>) {
        editQuestion?.cancel()
        val question = InputQuestion(title, body, tags as ArrayList<String>)
        editQuestion = apolloClient.mutate(EditQuestionMutation.builder()._id(_id).question(question.get()).build())
        editQuestion?.enqueue({
            val error = it.data()?.editQuestion()!!.error()
            if (error != null) {
                errorMessage.postValue(ErrorMessage(error.status()!!, error.statusCode()!!.toLong(), error.message()!!))
            } else {
                val datum = it.data()?.editQuestion()!!.command()
                result.postValue(Command(datum?.id()!!, datum?.status()!!, datum.modifyFlag()!!, datum.statusCode()!!, datum.message()!!))
            }
        }, {errorMessage.postValue(ErrorMessage("fail", 500, it.message!!))})
    }

    fun saveAnswer(questionId: String, body: String) {
        saveAnswer?.cancel()
        val answer = InputAnswer(questionId, body)
        saveAnswer = apolloClient.mutate(SaveAnswerMutation.builder().answer(answer.get()).build());
        saveAnswer?.enqueue({
            val error = it.data()?.createAnswer()?.error();
            if(error != null) {
                errorMessage.postValue(ErrorMessage(error.status()!!, error.statusCode()!!.toLong(), error.message()!!))
            } else {
                val datum = it.data()?.createAnswer()!!.command();
                result.postValue(Command(datum?.id()!!, questionId, datum.modifyFlag()!!, datum.statusCode()!!, body))
            }
        }, {errorMessage.postValue(ErrorMessage("fail", 500, it.message!!))})
    }

    fun editAnswer(_id: String, questionId: String, body: String) {
        editAnswer?.cancel()
        val answer = InputAnswer(questionId, body)
        editAnswer = apolloClient.mutate(EditAnswerMutation.builder()._id(_id).answer(answer.get()).build())
        editAnswer?.enqueue({
            val error = it.data()?.editAnswer()?.error()
            if(error != null) {
                errorMessage.postValue(ErrorMessage(error.status()!!, error.statusCode()!!.toLong(), error.message()!!))
            } else {
                val datum = it.data()?.editAnswer()!!.command()
                result.postValue(Command(datum?.id()!!, datum?.status()!!, datum.modifyFlag()!!, datum.statusCode()!!, datum.message()!!))
            }
        }, {errorMessage.postValue(ErrorMessage("fail", 500, it.message!!))})
    }

    fun saveQuestionComment(_id: String, body: String) {
        saveQuestionComment?.cancel()
        val comment = InputComment(prefs.userId, body)
        saveQuestionComment = apolloClient.mutate(SaveQuestionCommentMutation.builder()._id(_id).comment(comment.get()).build())
        saveQuestionComment?.enqueue({
            val error = it.data()?.createQuestionComment()?.error()
            if(error != null) {
                errorMessage.postValue(ErrorMessage(error.status()!!, error.statusCode()!!.toLong(), error.message()!!))
            } else {
                val datum = it.data()?.createQuestionComment()!!.command()
                result.postValue(Command(datum?.id()!!, datum?.status()!!, datum.modifyFlag()!!, datum.statusCode()!!, datum.message()!!))
            }
        }, {errorMessage.postValue(ErrorMessage("fail", 500, it.message!!))})
    }

    fun saveAnswerComment(_id: String, body: String) {
        saveAnswerComment?.cancel()
        val comment = InputComment(prefs.userId, body)
        saveAnswerComment = apolloClient.mutate(SaveAnswerCommentMutation.builder()._id(_id).comment(comment.get()).build())
        saveAnswerComment?.enqueue({
            val error = it.data()?.createAnswerComment()?.error()
            if(error != null) {
                errorMessage.postValue(ErrorMessage(error.status()!!, error.statusCode()!!.toLong(), error.message()!!))
            } else {
                val datum = it.data()?.createAnswerComment()!!.command()
                result.postValue(Command(datum?.id()!!, datum?.status()!!, datum.modifyFlag()!!, datum.statusCode()!!, datum.message()!!))
            }
        }, {errorMessage.postValue(ErrorMessage("fail", 500, it.message!!))})
    }

    override fun onCleared() {
        super.onCleared()
        saveQuestion?.cancel();
        editQuestion?.cancel()
        saveAnswer?.cancel()
        editAnswer?.cancel()
        saveQuestionComment?.cancel()
        saveAnswerComment?.cancel()
    }

}

