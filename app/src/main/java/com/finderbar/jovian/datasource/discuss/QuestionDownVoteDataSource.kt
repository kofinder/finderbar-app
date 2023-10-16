package com.finderbar.jovian.datasource.discuss

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PageKeyedDataSource
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.fetcher.ApolloResponseFetchers
import com.finderbar.jovian.datasource.convertQuestionDownVoteUser
import com.finderbar.jovian.models.*
import com.finderbar.jovian.utilities.NetworkState
import query.AllUserByQuestionDownVoteQuery

class QuestionDownVoteDataSource(private val apolloClient: ApolloClient, private val questionId: String): PageKeyedDataSource<Int, User>() {

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
        val query = setQuestionDownVoteQuery(InputCriteria(questionId, "", pageLimit, pageSkip))
        loadingLiveData.postValue(true)
        networkState.postValue(NetworkState.FETCHING)

        apolloClient.query(query).responseFetcher(ApolloResponseFetchers.NETWORK_FIRST)
            .enqueue(object : ApolloCall.Callback<AllUserByQuestionDownVoteQuery.Data>() {
                override fun onFailure(e: ApolloException) {
                    networkState.postValue(NetworkState.FAILURE)
                }

                override fun onResponse(response: Response<AllUserByQuestionDownVoteQuery.Data>) {
                    val error = response.data()?.allUserByQuestionDownVote()?.error()
                    if(error !== null) {
                        networkState.postValue(NetworkState.FAILURE)
                    } else {
                        val result = convertQuestionDownVoteUser(response.data()?.allUserByQuestionDownVote()?.users()!!)
                        val hasNext = response.data()?.allUserByQuestionDownVote()?.hasNext();
                        val totalCount = response.data()?.allUserByQuestionDownVote()?.totalCount()
                        networkState.postValue(NetworkState.SUCCESS)
                        emptyState.postValue(totalCount!! > 0)
                        processResult(UserSearchResult(result, hasNext!!, totalCount!!.toLong()))
                    }
                }
            })
    }

    private fun setQuestionDownVoteQuery(criteria: InputCriteria) = AllUserByQuestionDownVoteQuery.builder().criteria(criteria.get()).build()

}