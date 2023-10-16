package com.finderbar.jovian.datasource.post

import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PageKeyedDataSource
import com.apollographql.apollo.fetcher.ApolloResponseFetchers
import com.finderbar.jovian.models.*
import com.finderbar.jovian.utilities.NetworkState
import query.AllMoviesQuery

/**
 * Created by finderbar on 12/28/18.
 */
class MovieDataSource(private val apolloClient: ApolloClient, private val word: String): PageKeyedDataSource<Int, Movie>() {
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
        val query = setMovieQueryOffset(InputCriteria("", word, pageLimit, pageSkip))
        loadingLiveData.postValue(true)
        networkState.postValue(NetworkState.FETCHING)
        apolloClient.query(query).responseFetcher(ApolloResponseFetchers.NETWORK_FIRST)
                .enqueue(object : ApolloCall.Callback<AllMoviesQuery.Data>() {
                    override fun onResponse(response: Response<AllMoviesQuery.Data>) {
                        val error = response.data()?.allMovies()!!.error()
                        if (error !== null) {
                            networkState.postValue(NetworkState.FAILURE)
                        } else {
//                            val result = getResultList(response.data()?.allMovies()!!.movies()!!)
//                            val hasNext = response.data()?.allMovies()!!.hasNext();
//                            val totalCount = response.data()?.allMovies()!!.totalCount();
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


    private fun setMovieQueryOffset(criteria: InputCriteria) = AllMoviesQuery.builder().criteria(criteria.get()).build()

//    private fun getResultList(result: MutableList<AllMoviesQuery.Movie>): List<Movie> {
//        var list = ArrayList<Movie>()
//        result.forEach { list.add(Movie(
//            it._id(),
//            it.body()!!,
//            it.url(),
//            it.coverUrl(),
//            it.likeCount().toLong(),
//            it.commentCount().toLong(),
//            it.viewCount().toLong(),
//            it.userId(),
//            it.userAvatar(),
//            it.userName(),
//            it.createdAt()
//        ))}
//
//        return list
//    }
}

