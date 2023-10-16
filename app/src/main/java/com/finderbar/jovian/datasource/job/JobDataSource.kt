package com.finderbar.jovian.datasource.job

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PageKeyedDataSource
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.fetcher.ApolloResponseFetchers
import com.finderbar.jovian.datasource.getResultList
import com.finderbar.jovian.models.*
import com.finderbar.jovian.utilities.NetworkState
import query.AllJobsQuery

class JobDataSource (private val apolloClient: ApolloClient, private val word: String): PageKeyedDataSource<Int, Job>() {
    val networkState = MutableLiveData<NetworkState>()
    val loadingInitial = MutableLiveData<Boolean>()
    val loadingBefore = MutableLiveData<Boolean>()
    val loadingAfter = MutableLiveData<Boolean>()

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Job>) {
        load(params.requestedLoadSize, 0, loadingInitial) { result ->
            if (result.hasNext) {
                callback.onResult(result.jobs, null, result.jobs.size)
            } else {
                callback.onResult(result.jobs, null, null)
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Job>) {
        load(params.requestedLoadSize, params.key, loadingAfter) { result ->
            if (result.hasNext) {
                callback.onResult(result.jobs, params.key + result.jobs.size)
            } else {
                callback.onResult(result.jobs, null)
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Job>) {
        load(params.requestedLoadSize, params.key, loadingBefore) { result ->
            if (result.hasNext) {
                callback.onResult(result.jobs, params.key - result.jobs.size)
            } else {
                callback.onResult(result.jobs, null)
            }
        }
    }

    private fun load(pageLimit: Int, pageSkip: Int, loadingLiveData: MutableLiveData<Boolean>, processResult: (JobSearchResult) -> Unit ) {
        val query = setJobQueryOffset(InputCriteria("", word, pageLimit, pageSkip))
        loadingLiveData.postValue(true)
        networkState.postValue(NetworkState.FETCHING)
        apolloClient.query(query).responseFetcher(ApolloResponseFetchers.NETWORK_FIRST)
                .enqueue(object : ApolloCall.Callback<AllJobsQuery.Data>() {
                    override fun onResponse(response: Response<AllJobsQuery.Data>) {
                        val error = response.data()?.allJobs()!!.error()
                        if (error !== null) {
                            networkState.postValue(NetworkState.FAILURE)
                        } else {
                            val result = getResultList(response.data()?.allJobs()!!.jobs()!!)
                            val hasNext = response.data()?.allJobs()!!.hasNext();
                            val totalCount = response.data()?.allJobs()!!.totalCount();
                            networkState.postValue(NetworkState.SUCCESS)
                            processResult(JobSearchResult(result, hasNext!!, totalCount!!.toLong()))
                        }
                    }
                    override fun onFailure(e: ApolloException) {
                        networkState.postValue(NetworkState.FAILURE)
                    }
                })

        loadingLiveData.postValue(false)
    }

    private fun setJobQueryOffset(criteria: InputCriteria) = AllJobsQuery.builder().criteria(criteria.get()).build()
}