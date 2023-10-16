package com.finderbar.jovian.viewmodels.discuss

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import com.finderbar.jovian.apolloClient
import com.finderbar.jovian.utilities.NetworkState
import com.finderbar.jovian.datasource.discuss.TagDataSourceFactory


/**
 * Created by thein on 1/12/19.
 */
class TagsVM : ViewModel()  {

    private val dataSourceFactory = run { TagDataSourceFactory(apolloClient) }

    val tagList = run {
        val pagedListConfig = PagedList.Config.Builder().setInitialLoadSizeHint(10).setPageSize(10).build()
        LivePagedListBuilder(dataSourceFactory, pagedListConfig).build()
    }

    val loadingInitial: LiveData<Boolean> = dataSourceFactory.loadingInitial
    val loadingBefore: LiveData<Boolean> = dataSourceFactory.loadingBefore
    val loadingAfter: LiveData<Boolean> = dataSourceFactory.loadingAfter
    val networkState: LiveData<NetworkState> = dataSourceFactory.networkState

    fun refresh() {
        dataSourceFactory.refresh()
    }

    fun setQuery(word: String) {
        dataSourceFactory.query = word
    }

    override fun onCleared() {
        super.onCleared()
    }
}