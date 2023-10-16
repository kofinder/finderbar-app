package com.finderbar.jovian.datasource.discuss

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PageKeyedDataSource
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.fetcher.ApolloResponseFetchers
import com.finderbar.jovian.models.*
import com.finderbar.jovian.utilities.NetworkState
import query.AllTagsQuery
import java.util.logging.Logger

/**
 * Created by FinderBar on 1/12/19.
 */
class TagDataSource(private val apolloClient: ApolloClient, private val query: String): PageKeyedDataSource<Int, Tags>() {
    companion object {
        var Log = Logger.getLogger(TagDataSource::class.java.name)
    }

    val networkState = MutableLiveData<NetworkState>()
    val loadingInitial = MutableLiveData<Boolean>()
    val loadingBefore = MutableLiveData<Boolean>()
    val loadingAfter = MutableLiveData<Boolean>()

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Tags>) {
        load(params.requestedLoadSize, 0, loadingInitial) { result ->
            if (result.hasNext) {
                callback.onResult(result.tagList, null, result.tagList.size)
            } else {
                callback.onResult(result.tagList, null, null)
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Tags>) {
        load(params.requestedLoadSize, params.key, loadingAfter) { result ->
            if (result.hasNext) {
                callback.onResult(result.tagList, params.key + result.tagList.size)
            } else {
                callback.onResult(result.tagList, null)
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Tags>) {
        load(params.requestedLoadSize, params.key, loadingBefore) { result ->
            if (result.hasNext) {
                callback.onResult(result.tagList, params.key - result.tagList.size)
            } else {
                callback.onResult(result.tagList, null)
            }
        }
    }

    private fun load(pageLimit: Int, pageSkip: Int, loadingLiveData: MutableLiveData<Boolean>, processResult: (TagSearchResult) -> Unit ) {
        val query = setTagQueryOffset(InputCriteria("", query, pageLimit, pageSkip))
        loadingLiveData.postValue(true)
        networkState.postValue(NetworkState.FETCHING)
        apolloClient.query(query).responseFetcher(ApolloResponseFetchers.NETWORK_FIRST)
        .enqueue(object : ApolloCall.Callback<AllTagsQuery.Data>() {
            override fun onResponse(response: Response<AllTagsQuery.Data>) {
                val error = response.data()?.allTags()?.error();
                if (error !== null) {
                    networkState.postValue(NetworkState.FAILURE)
                } else {
                    val result = getResultList(response.data()?.allTags()!!.tags()!!)
                    val hasNext = response.data()?.allTags()?.hasNext();
                    val totalCount = response.data()?.allTags()?.totalCount()
                    networkState.postValue(NetworkState.SUCCESS)
                    processResult(TagSearchResult(result, hasNext!!, totalCount!!.toLong()))
                }
            }
            override fun onFailure(e: ApolloException) {
                networkState.postValue(NetworkState.FAILURE)
            }
        })

        loadingLiveData.postValue(false)
    }

    private fun setTagQueryOffset(criteria: InputCriteria) = AllTagsQuery.builder().criteria(criteria.get()).build()

    private fun getResultList(result: MutableList<AllTagsQuery.Tag>): List<Tags> {
        var list = ArrayList<Tags>()
        result.forEach { list.add(Tags(it.tagName(), it.tagCount().toString()))}
        return list
    }
}
