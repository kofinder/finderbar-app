package com.finderbar.jovian.datasource.user

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PageKeyedDataSource
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.fetcher.ApolloResponseFetchers
import com.finderbar.jovian.datasource.convertUserAnswer
import com.finderbar.jovian.models.*
import com.finderbar.jovian.utilities.NetworkState
import query.AllAnswerByUserQuery

class UserAnswerDataSource(private val apolloClient: ApolloClient, private val userId: String):
        PageKeyedDataSource<Int, Answer>() {

    val networkState = MutableLiveData<NetworkState>()
    val loadingInitial = MutableLiveData<Boolean>()
    val loadingBefore = MutableLiveData<Boolean>()
    val loadingAfter = MutableLiveData<Boolean>()


    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Answer>) {
        load(params.requestedLoadSize, 0, loadingInitial) { result ->
            if (result.hasNext) {
                callback.onResult(result.answers, null, result.answers.size)
            } else {
                callback.onResult(result.answers, null, null)
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Answer>) {
        load(params.requestedLoadSize, params.key, loadingAfter) { result ->
            if (result.hasNext) {
                callback.onResult(result.answers, params.key + result.answers.size)
            } else {
                callback.onResult(result.answers, null)
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Answer>) {
        load(params.requestedLoadSize, params.key, loadingBefore) { result ->
            if (result.hasNext) {
                callback.onResult(result.answers, params.key - result.answers.size)
            } else {
                callback.onResult(result.answers, null)
            }
        }
    }

    private fun load(pageLimit: Int, pageSkip: Int, loadingLiveData: MutableLiveData<Boolean>, processResult: (AnswerResult) -> Unit ) {
        val query = setUserAnswerQuery(InputCriteria(userId, "", pageLimit, pageSkip))
        loadingLiveData.postValue(true)
        networkState.postValue(NetworkState.FETCHING)

        apolloClient.query(query).responseFetcher(ApolloResponseFetchers.NETWORK_FIRST)
                .enqueue(object : ApolloCall.Callback<AllAnswerByUserQuery.Data>() {
                    override fun onFailure(e: ApolloException) {
                        networkState.postValue(NetworkState.FAILURE)
                    }

                    override fun onResponse(response: Response<AllAnswerByUserQuery.Data>) {
                        val error = response.data()?.allAnswerByUser()?.error()
                        if(error !== null) {
                            networkState.postValue(NetworkState.FAILURE)
                        } else {
                            val result = convertUserAnswer(response.data()?.allAnswerByUser()?.answers()!!)
                            val hasNext = response.data()?.allAnswerByUser()?.hasNext();
                            val totalCount = response.data()?.allAnswerByUser()?.totalCount()
                            networkState.postValue(NetworkState.SUCCESS)
                            processResult(AnswerResult(result, hasNext!!, totalCount!!.toLong()))
                        }
                    }
                })
    }

    private fun setUserAnswerQuery(criteria: InputCriteria) = AllAnswerByUserQuery.builder().criteria(criteria.get()).build()

}