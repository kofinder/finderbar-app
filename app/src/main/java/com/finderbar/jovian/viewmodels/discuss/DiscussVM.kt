package com.finderbar.jovian.viewmodels.discuss

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import com.finderbar.jovian.apolloClient
import com.finderbar.jovian.datasource.discuss.DiscussDataSourceFactory
import com.finderbar.jovian.utilities.NetworkState

class DiscussVM: ViewModel() {

    private var dataSourceFactory = run { DiscussDataSourceFactory(apolloClient) }

    val loadingInitial: LiveData<Boolean> = dataSourceFactory.loadingInitial
    val loadingBefore: LiveData<Boolean> = dataSourceFactory.loadingBefore
    val loadingAfter: LiveData<Boolean> = dataSourceFactory.loadingAfter
    val networkState: LiveData<NetworkState> = dataSourceFactory.networkState

    val discuss = run {
        val config = PagedList.Config.Builder().setInitialLoadSizeHint(20).setPageSize(20).build()
        LivePagedListBuilder(dataSourceFactory, config).build()
    }

    fun setId(questionId: String) {
        dataSourceFactory._id = questionId
    }

    fun refresh() {
        dataSourceFactory.refresh()
    }

    override fun onCleared() {
        super.onCleared()
    }

}