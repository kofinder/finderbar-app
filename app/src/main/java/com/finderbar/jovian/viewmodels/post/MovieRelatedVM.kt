package com.finderbar.jovian.viewmodels.post

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import com.finderbar.jovian.apolloClient
import com.finderbar.jovian.datasource.post.MovieRelatedDataSourceFactory
import com.finderbar.jovian.utilities.NetworkState

class MovieRelatedVM: ViewModel() {

    private val dataSourceFactory = run { MovieRelatedDataSourceFactory(apolloClient) }

    val movies = run {
        val pagedListConfig = PagedList.Config.Builder().setEnablePlaceholders(false).setInitialLoadSizeHint(10).setPageSize(10).build()
        LivePagedListBuilder(dataSourceFactory, pagedListConfig).build()
    }

    val loadingInitial: LiveData<Boolean> = dataSourceFactory.loadingInitial
    val loadingBefore: LiveData<Boolean> = dataSourceFactory.loadingBefore
    val loadingAfter: LiveData<Boolean> = dataSourceFactory.loadingAfter
    val networkState: LiveData<NetworkState> = dataSourceFactory.networkState

    fun setQuery(userId: String) {
        dataSourceFactory.userId = userId
    }

    fun refresh() {
        dataSourceFactory.refresh()
    }

    override fun onCleared() {
        super.onCleared()
    }
}