package com.finderbar.jovian.datasource.post

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PageKeyedDataSource
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.fetcher.ApolloResponseFetchers
import com.finderbar.jovian.models.*
import com.finderbar.jovian.utilities.AppConstants.TYPE_POST_PHOTO
import com.finderbar.jovian.utilities.AppConstants.TYPE_POST_TEXT
import com.finderbar.jovian.utilities.AppConstants.TYPE_POST_VIDEO
import com.finderbar.jovian.utilities.NetworkState
import query.AllPostsQuery

class PostDataSource(private val apolloClient: ApolloClient, private val word: String): PageKeyedDataSource<Int, Post>() {
    val networkState = MutableLiveData<NetworkState>()
    val loadingInitial = MutableLiveData<Boolean>()
    val loadingBefore = MutableLiveData<Boolean>()
    val loadingAfter = MutableLiveData<Boolean>()

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Post>) {
        load(params.requestedLoadSize, 0, loadingInitial) { result ->
            if (result.hasNext) {
                callback.onResult(result.posts, null, result.posts.size)
            } else {
                callback.onResult(result.posts, null, null)
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Post>) {
        load(params.requestedLoadSize, params.key, loadingAfter) { result ->
            if (result.hasNext) {
                callback.onResult(result.posts, params.key + result.posts.size)
            } else {
                callback.onResult(result.posts, null)
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Post>) {
        load(params.requestedLoadSize, params.key, loadingBefore) { result ->
            if (result.hasNext) {
                callback.onResult(result.posts, params.key - result.posts.size)
            } else {
                callback.onResult(result.posts, null)
            }
        }
    }

    private fun load(pageLimit: Int, pageSkip: Int, loadingLiveData: MutableLiveData<Boolean>, processResult: (PostSearchResult) -> Unit ) {
        val query = setPostQueryOffset(InputCriteria("", word, pageLimit, pageSkip))
        loadingLiveData.postValue(true)
        networkState.postValue(NetworkState.FETCHING)
        apolloClient.query(query).responseFetcher(ApolloResponseFetchers.NETWORK_FIRST)
                .enqueue(object : ApolloCall.Callback<AllPostsQuery.Data>() {
                    override fun onResponse(response: Response<AllPostsQuery.Data>) {
                        val error = response.data()?.allPosts()!!.error()
                        if (error !== null) {
                            networkState.postValue(NetworkState.FAILURE)
                        } else {
                            val result = getResultList(response.data()?.allPosts()?.posts()!!)
                            val hasNext = response.data()?.allPosts()?.hasNext();
                            val totalCount = response.data()?.allPosts()?.totalCount();
                            networkState.postValue(NetworkState.SUCCESS)
                            processResult(PostSearchResult(result, hasNext!!, totalCount!!.toLong()))
                        }
                    }
                    override fun onFailure(e: ApolloException) {
                        networkState.postValue(NetworkState.FAILURE)
                    }
                })

        loadingLiveData.postValue(false)
    }


    private fun setPostQueryOffset(criteria: InputCriteria) = AllPostsQuery.builder().criteria(criteria.get()).build()

    private fun getResultList(result: MutableList<AllPostsQuery.Post>): List<Post> {
        var list = ArrayList<Post>()
        var postType: Int = 0;
        result.forEach {
            postType = when {
                it.images()?.isNotEmpty()!! -> {
                    TYPE_POST_PHOTO
                }
                it.movies()?.isNotEmpty()!! -> {
                    TYPE_POST_VIDEO
                }
                else -> TYPE_POST_TEXT
            }
            val post = Post(
                it._id(),
                it.userId(),
        it.title()?.toString() ?: "",
        it.body()?.toString() ?: "",
                getImages(it.images()!!),
                getMovies(it.movies()!!),
                emptyList(),
                it.likeCount(),
                it.commentCount(),
                it.viewCount(),
                it.userAvatar(),
                it.userName(),
                it.createdAt(),
                postType
            )
            list.add(post);
        }

        return list
    }

    private fun getImages(images: MutableList<AllPostsQuery.Image>): List<PostImage> {
        val result = ArrayList<PostImage>()
        images.forEach{result.add(PostImage("", "", "", it.url()))}

        return  result
    }

    private fun getMovies(movies: MutableList<AllPostsQuery.Movie>): List<Movie> {
        val result = ArrayList<Movie>()
        movies.forEach{result.add(Movie("", "", it.url(), it.url()))}

        return  result
    }
}