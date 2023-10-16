package com.finderbar.jovian.datasource.tutorial
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PageKeyedDataSource
import com.apollographql.apollo.fetcher.ApolloResponseFetchers
import com.finderbar.jovian.datasource.convertCategory
import com.finderbar.jovian.models.*
import com.finderbar.jovian.utilities.NetworkState
import query.AllCategoriesQuery

/**
 * Created by finderbar on 12/28/18.
 */
class CategoryDataSource(private val apolloClient: ApolloClient, private val word: String): PageKeyedDataSource<Int, Category>() {
    val networkState = MutableLiveData<NetworkState>()
    val loadingInitial = MutableLiveData<Boolean>()
    val loadingBefore = MutableLiveData<Boolean>()
    val loadingAfter = MutableLiveData<Boolean>()

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Category>) {
        load(params.requestedLoadSize, 0, loadingInitial) { result ->
            if (result.hasNext) {
                callback.onResult(result.movies, null, result.movies.size)
            } else {
                callback.onResult(result.movies, null, null)
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Category>) {
        load(params.requestedLoadSize, params.key, loadingAfter) { result ->
            if (result.hasNext) {
                callback.onResult(result.movies, params.key + result.movies.size)
            } else {
                callback.onResult(result.movies, null)
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Category>) {
        load(params.requestedLoadSize, params.key, loadingBefore) { result ->
            if (result.hasNext) {
                callback.onResult(result.movies, params.key - result.movies.size)
            } else {
                callback.onResult(result.movies, null)
            }
        }
    }

    private fun load(pageLimit: Int, pageSkip: Int, loadingLiveData: MutableLiveData<Boolean>, processResult: (CategorySearchResult) -> Unit ) {
        val query = setCategoryQueryOffset(InputCriteria("", word, pageLimit, pageSkip))
        loadingLiveData.postValue(true)
        networkState.postValue(NetworkState.FETCHING)
        apolloClient.query(query).responseFetcher(ApolloResponseFetchers.NETWORK_FIRST)
            .enqueue(object : ApolloCall.Callback<AllCategoriesQuery.Data>() {
                override fun onResponse(response: Response<AllCategoriesQuery.Data>) {
                    val error = response.data()?.allCategories()!!.error()
                    if (error !== null) {
                        networkState.postValue(NetworkState.FAILURE)
                    } else {
                        val result = convertCategory(response.data()?.allCategories()!!.categories()!!)
                        val hasNext = response.data()?.allCategories()!!.hasNext();
                        val totalCount = response.data()?.allCategories()!!.totalCount();
                        networkState.postValue(NetworkState.SUCCESS)
                        processResult(CategorySearchResult(result, hasNext!!, totalCount!!.toLong()))
                    }
                }
                override fun onFailure(e: ApolloException) {
                    networkState.postValue(NetworkState.FAILURE)
                }
            })
        loadingLiveData.postValue(false)
    }


    private fun setCategoryQueryOffset(criteria: InputCriteria) = AllCategoriesQuery.builder().criteria(criteria.get()).build()
}