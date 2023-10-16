package com.finderbar.jovian.datasource.discuss

import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PageKeyedDataSource
import com.apollographql.apollo.fetcher.ApolloResponseFetchers
import com.finderbar.jovian.datasource.convertQuestions
import com.finderbar.jovian.models.*
import com.finderbar.jovian.utilities.NetworkState
import query.AllQuestionsQuery

/**
 * Created by finderbar on 12/28/18.
 */
class QuestionDataSource(private val apolloClient: ApolloClient, private val word: String): PageKeyedDataSource<Int, Question>() {
    val networkState = MutableLiveData<NetworkState>()
    val loadingInitial = MutableLiveData<Boolean>()
    val loadingBefore = MutableLiveData<Boolean>()
    val loadingAfter = MutableLiveData<Boolean>()

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Question>) {
        load(params.requestedLoadSize, 0, loadingInitial) { result ->
            if (result.hasNext) {
                callback.onResult(result.questions, null, result.questions.size)
            } else {
                callback.onResult(result.questions, null, null)
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Question>) {
        load(params.requestedLoadSize, params.key, loadingAfter) { result ->
            if (result.hasNext) {
                callback.onResult(result.questions, params.key + result.questions.size)
            } else {
                callback.onResult(result.questions, null)
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Question>) {
        load(params.requestedLoadSize, params.key, loadingBefore) { result ->
            if (result.hasNext) {
                callback.onResult(result.questions, params.key - result.questions.size)
            } else {
                callback.onResult(result.questions, null)
            }
        }
    }

    private fun load(pageLimit: Int, pageSkip: Int, loadingLiveData: MutableLiveData<Boolean>, processResult: (QuestionSearchResult) -> Unit) {
        val query = setQuestionQueryLimitWithOffset(InputCriteria("", word, pageLimit, pageSkip))
        loadingLiveData.postValue(true)
        networkState.postValue(NetworkState.FETCHING)
        apolloClient.query(query).responseFetcher(ApolloResponseFetchers.NETWORK_FIRST)
                .enqueue(object : ApolloCall.Callback<AllQuestionsQuery.Data>() {
                    override fun onResponse(response: Response<AllQuestionsQuery.Data>) {
                        val error = response.data()?.allQuestions()?.error()
                        if (error !== null) {
                            networkState.postValue(NetworkState.FAILURE)
                        } else {
                            val result = convertQuestions(response.data()?.allQuestions()?.questions()!!)
                            val hasNext = response.data()?.allQuestions()?.hasNext();
                            val totalCount = response.data()?.allQuestions()?.totalCount()
                            networkState.postValue(NetworkState.SUCCESS)
                            processResult(QuestionSearchResult(result, hasNext!!, totalCount!!.toLong()))
                        }
                    }

                    override fun onFailure(e: ApolloException) {
                        networkState.postValue(NetworkState.FAILURE)
                    }
                })

        loadingLiveData.postValue(false)
    }


    private fun setQuestionQueryLimitWithOffset(criteria: InputCriteria) = AllQuestionsQuery.builder().criteria(criteria.get()).build()

}
