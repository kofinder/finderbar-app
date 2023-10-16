package com.finderbar.jovian.viewmodels.tutorial

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.fetcher.ApolloResponseFetchers
import com.finderbar.jovian.apolloClient
import com.finderbar.jovian.enqueue
import com.finderbar.jovian.models.ErrorMessage
import com.finderbar.jovian.models.Tutorial
import query.AllTutoriesQuery

class TutorialVM: ViewModel() {
    var errorMessage: MutableLiveData<ErrorMessage> = MutableLiveData();
    var tutorialList: MutableLiveData<List<Tutorial>>? = MutableLiveData();
    private var tutorialQueryCall: ApolloCall<AllTutoriesQuery.Data>? = null

    fun setCategoryId(categoryId: String) {
        val query = setTutorialQuery(categoryId)
        tutorialQueryCall?.cancel()
        tutorialQueryCall = apolloClient.query(query).responseFetcher(ApolloResponseFetchers.NETWORK_FIRST)
        tutorialQueryCall?.enqueue({
            val error = it.data()!!.allTutotrials().error();
            if (error != null) {
                errorMessage.postValue(error.message()?.let { it1 -> ErrorMessage("fail", 500, it1) })
            } else {
                val result = getResult(it.data()!!.allTutotrials().tutorials()!!)
                tutorialList?.postValue(result)
            }
        }, {errorMessage.postValue(it.message?.let { it1 -> ErrorMessage("fail", 500, it1) })})

    }


    private fun setTutorialQuery(categoryId: String) = AllTutoriesQuery.builder().categoryId(categoryId).build()

    private fun getResult(result: MutableList<AllTutoriesQuery.Tutorial>) : ArrayList<Tutorial> {
        var list = ArrayList<Tutorial>()
        result.forEach {list.add(Tutorial(
                it._id(),
                it.titleText(),
                it.htmlBody()!!,
                it.serializedBody()!!,
                it.commentCount(),
                it.viewCount(),
                it.createdAt()
        ))}

        return list
    }

    override fun onCleared() {
        super.onCleared()
        tutorialQueryCall?.cancel()
    }
}