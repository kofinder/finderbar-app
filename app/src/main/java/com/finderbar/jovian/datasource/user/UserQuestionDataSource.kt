package com.finderbar.jovian.datasource.user

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PageKeyedDataSource
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.fetcher.ApolloResponseFetchers
import com.finderbar.jovian.datasource.convertUserQuestions
import com.finderbar.jovian.models.*
import com.finderbar.jovian.utilities.NetworkState
import query.AllQuestionByUserQuery

class UserQuestionDataSource(private val apolloClient: ApolloClient, private val userId: String): PageKeyedDataSource<Int, Question>() {

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

    private fun load(pageLimit: Int, pageSkip: Int, loadingLiveData: MutableLiveData<Boolean>, processResult: (QuestionSearchResult) -> Unit ) {
        val query = setUserQuestionQuery(InputCriteria(userId, "", pageLimit, pageSkip))
        loadingLiveData.postValue(true)
        networkState.postValue(NetworkState.FETCHING)

        apolloClient.query(query).responseFetcher(ApolloResponseFetchers.NETWORK_FIRST)
                .enqueue(object : ApolloCall.Callback<AllQuestionByUserQuery.Data>() {
                    override fun onFailure(e: ApolloException) {
                        networkState.postValue(NetworkState.FAILURE)
                    }

                    override fun onResponse(response: Response<AllQuestionByUserQuery.Data>) {
                        val error = response.data()?.allQuestionByUser()?.error()
                        if(error !== null) {
                            networkState.postValue(NetworkState.FAILURE)
                        } else {
                            val result = convertUserQuestions(response.data()?.allQuestionByUser()?.questions()!!)
                            val hasNext = response.data()?.allQuestionByUser()?.hasNext();
                            val totalCount = response.data()?.allQuestionByUser()?.totalCount()
                            networkState.postValue(NetworkState.SUCCESS)
                            processResult(QuestionSearchResult(result, hasNext!!, totalCount!!.toLong()))
                        }
                    }
                })
    }

    private fun setUserQuestionQuery(criteria: InputCriteria) = AllQuestionByUserQuery.builder().criteria(criteria.get()).build()

}