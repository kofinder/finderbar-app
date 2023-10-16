package com.finderbar.jovian.datasource.post

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PageKeyedDataSource
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.fetcher.ApolloResponseFetchers
import com.finderbar.jovian.models.*
import com.finderbar.jovian.utilities.NetworkState
import query.AllRelatedMoviesQuery

class MovieRelatedDataSource(private val apolloClient: ApolloClient, private val userId: String): PageKeyedDataSource<Int, Movie>() {
    val networkState = MutableLiveData<NetworkState>()
    val loadingInitial = MutableLiveData<Boolean>()
    val loadingBefore = MutableLiveData<Boolean>()
    val loadingAfter = MutableLiveData<Boolean>()
    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Movie>) {
        load(params.requestedLoadSize, 0, loadingInitial) { result ->
            if (result.hasNext) {
                callback.onResult(result.movies, null, result.movies.size)
            } else {
                callback.onResult(result.movies, null, null)
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {
        load(params.requestedLoadSize, params.key, loadingAfter) { result ->
            if (result.hasNext) {
                callback.onResult(result.movies, params.key + result.movies.size)
            } else {
                callback.onResult(result.movies, null)
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {
        load(params.requestedLoadSize, params.key, loadingBefore) { result ->
            if (result.hasNext) {
                callback.onResult(result.movies, params.key - result.movies.size)
            } else {
                callback.onResult(result.movies, null)
            }
        }
    }

    private fun load(pageLimit: Int, pageSkip: Int, loadingLiveData: MutableLiveData<Boolean>, processResult: (MovieSearchResult) -> Unit ) {
        val query = setMovieQueryOffset(InputCriteria(userId, "",10, 0))
        loadingLiveData.postValue(true)
        networkState.postValue(NetworkState.FETCHING)
        apolloClient.query(query).responseFetcher(ApolloResponseFetchers.NETWORK_FIRST)
                .enqueue(object : ApolloCall.Callback<AllRelatedMoviesQuery.Data>() {
                    override fun onResponse(response: Response<AllRelatedMoviesQuery.Data>) {
                        val error = response.data()?.allRelatedMovies()!!.error()
                        if (error !== null) {
                            networkState.postValue(NetworkState.FAILURE)
                        } else {
//                            val result = getResultList(response.data()?.allRelatedMovies()!!.movies()!!)
//                            val hasNext = response.data()?.allRelatedMovies()!!.hasNext();
//                            val totalCount = response.data()?.allRelatedMovies()!!.totalCount();
//                            networkState.postValue(NetworkState.SUCCESS)
//                            processResult(MovieSearchResult(result, hasNext!!, totalCount!!.toLong()))
                        }
                    }
                    override fun onFailure(e: ApolloException) {
                        networkState.postValue(NetworkState.FAILURE)
                    }
                })

        loadingLiveData.postValue(false)
    }

    private fun setMovieQueryOffset(criteria: InputCriteria) = AllRelatedMoviesQuery.builder().criteria(criteria.get()).build()
}



