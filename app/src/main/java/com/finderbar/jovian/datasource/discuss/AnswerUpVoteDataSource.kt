package com.finderbar.jovian.datasource.discuss

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PageKeyedDataSource
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.fetcher.ApolloResponseFetchers
import com.finderbar.jovian.datasource.convertAnswerUpVoteUser
import com.finderbar.jovian.models.*
import com.finderbar.jovian.utilities.NetworkState
import query.AllUserByAnswerUpVoteQuery


class AnswerUpVoteDataSource(private val apolloClient: ApolloClient, private val answerId: String): PageKeyedDataSource<Int, User>() {

    val networkState = MutableLiveData<NetworkState>()
    val loadingInitial = MutableLiveData<Boolean>()
    val loadingBefore = MutableLiveData<Boolean>()
    val loadingAfter = MutableLiveData<Boolean>()
    val emptyState = MutableLiveData<Boolean>()

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, User>) {
        load(params.requestedLoadSize, 0, loadingInitial) { result ->
            if (result.hasNext) {
                callback.onResult(result.users, null, result.users.size)
            } else {
                callback.onResult(result.users, null, null)
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, User>) {
        load(params.requestedLoadSize, params.key, loadingAfter) { result ->
            if (result.hasNext) {
                callback.onResult(result.users, params.key + result.users.size)
            } else {
                callback.onResult(result.users, null)
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, User>) {
        load(params.requestedLoadSize, params.key, loadingBefore) { result ->
            if (result.hasNext) {
                callback.onResult(result.users, params.key - result.users.size)
            } else {
                callback.onResult(result.users, null)
            }
        }
    }

    private fun load(pageLimit: Int, pageSkip: Int, loadingLiveData: MutableLiveData<Boolean>, processResult: (UserSearchResult) -> Unit ) {
        val query = setQuestionUpVoteQuery(InputCriteria(answerId, "", pageLimit, pageSkip))
        loadingLiveData.postValue(true)
        networkState.postValue(NetworkState.FETCHING)

        apolloClient.query(query).responseFetcher(ApolloResponseFetchers.NETWORK_FIRST)
            .enqueue(object : ApolloCall.Callback<AllUserByAnswerUpVoteQuery.Data>() {
                override fun onFailure(e: ApolloException) {
                    networkState.postValue(NetworkState.FAILURE)
                }

                override fun onResponse(response: Response<AllUserByAnswerUpVoteQuery.Data>) {
                    val error = response.data()?.allUserByAnswerUpVote()?.error()
                    if(error !== null) {
                        networkState.postValue(NetworkState.FAILURE)
                    } else {
                        val result = convertAnswerUpVoteUser(response.data()?.allUserByAnswerUpVote()?.users()!!)
                        val hasNext = response.data()?.allUserByAnswerUpVote()?.hasNext();
                        val totalCount = response.data()?.allUserByAnswerUpVote()?.totalCount()
                        networkState.postValue(NetworkState.SUCCESS)
                        processResult(UserSearchResult(result, hasNext!!, totalCount!!.toLong()))
                    }
                }
            })
    }

    private fun setQuestionUpVoteQuery(criteria: InputCriteria) = AllUserByAnswerUpVoteQuery.builder().criteria(criteria.get()).build()
}