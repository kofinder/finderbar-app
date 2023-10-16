package com.finderbar.jovian.viewmodels.post

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import com.finderbar.jovian.apolloClient
import com.finderbar.jovian.datasource.post.PostDataSourceFactory
import com.finderbar.jovian.utilities.NetworkState

class PostVM : ViewModel() {

    private val dataSourceFactory = run { PostDataSourceFactory(apolloClient) }

    val posts = run {
        val pagedListConfig = PagedList.Config.Builder().setEnablePlaceholders(false).setInitialLoadSizeHint(10).setPageSize(10).build()
        LivePagedListBuilder(dataSourceFactory, pagedListConfig).build()
    }

    val loadingInitial: LiveData<Boolean> = dataSourceFactory.loadingInitial
    val loadingBefore: LiveData<Boolean> = dataSourceFactory.loadingBefore
    val loadingAfter: LiveData<Boolean> = dataSourceFactory.loadingAfter
    val networkState: LiveData<NetworkState> = dataSourceFactory.networkState

    fun setQuery(word: String) {
        dataSourceFactory.word = word
    }

    fun refresh() {
        dataSourceFactory.refresh()
    }
}