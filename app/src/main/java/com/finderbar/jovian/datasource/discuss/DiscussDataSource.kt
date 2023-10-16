package com.finderbar.jovian.datasource.discuss

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PageKeyedDataSource
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.fetcher.ApolloResponseFetchers
import com.finderbar.jovian.datasource.convertDiscuss
import com.finderbar.jovian.models.*
import com.finderbar.jovian.utilities.NetworkState
import query.AllDiscussQuery

class DiscussDataSource(private val apolloClient: ApolloClient, private val _id: String): PageKeyedDataSource<Int, Discuss>() {

    val networkState = MutableLiveData<NetworkState>()
    val loadingInitial = MutableLiveData<Boolean>()
    val loadingBefore = MutableLiveData<Boolean>()
    val loadingAfter = MutableLiveData<Boolean>()

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Discuss>) {
        load(params.requestedLoadSize, 0, loadingInitial) { result ->
            if (result.hasNext) {
                callback.onResult(result.discuss, null, result.discuss.size)
            } else {
                callback.onResult(result.discuss, null, null)
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Discuss>) {
        load(params.requestedLoadSize, params.key, loadingAfter) { result ->
            if (result.hasNext) {
                callback.onResult(result.discuss, params.key + result.discuss.size)
            } else {
                callback.onResult(result.discuss, null)
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Discuss>) {
        load(params.requestedLoadSize, params.key, loadingBefore) { result ->
            if (result.hasNext) {
                callback.onResult(result.discuss, params.key - result.discuss.size)
            } else {
                callback.onResult(result.discuss, null)
            }
        }
    }

    private fun load(pageLimit: Int, pageSkip: Int, loadingLiveData: MutableLiveData<Boolean>, processResult: (DiscussResult) -> Unit ) {
        val query = setDiscussQuery(InputCriteria(_id, "", pageLimit, pageSkip))
        loadingLiveData.postValue(true)
        networkState.postValue(NetworkState.FETCHING)

        apolloClient.query(query).responseFetcher(ApolloResponseFetchers.NETWORK_FIRST)
                .enqueue(object : ApolloCall.Callback<AllDiscussQuery.Data>() {
                    override fun onFailure(e: ApolloException) {
                        networkState.postValue(NetworkState.FAILURE)
                    }
                    override fun onResponse(response: Response<AllDiscussQuery.Data>) {
                        val error = response.data()?.allDiscuss()?.error()
                        if(error !== null) {
                            networkState.postValue(NetworkState.FAILURE)
                        } else {
                            val result = convertDiscuss(response.data()?.allDiscuss()?.discuss()!!)
                            val hasNext = response.data()?.allDiscuss()?.hasNext();
                            val totalCount = response.data()?.allDiscuss()?.totalCount()
                            networkState.postValue(NetworkState.SUCCESS)
                            processResult(DiscussResult(result, hasNext!!, totalCount!!.toLong()))
                        }
                    }
                })
    }

    private fun setDiscussQuery(criteria: InputCriteria) = AllDiscussQuery.builder().criteria(criteria.get()).build();
}
