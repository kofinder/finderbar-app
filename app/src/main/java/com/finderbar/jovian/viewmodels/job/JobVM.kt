package com.finderbar.jovian.viewmodels.job

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import com.finderbar.jovian.apolloClient
import com.finderbar.jovian.datasource.job.JobDataSourceFactory
import com.finderbar.jovian.utilities.NetworkState

class JobVM : ViewModel()  {

    private val dataSourceFactory = run { JobDataSourceFactory(apolloClient) }

    val jobs = run {
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
