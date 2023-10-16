package com.finderbar.jovian.viewmodels.user

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import com.finderbar.jovian.apolloClient
import com.finderbar.jovian.utilities.NetworkState
import com.finderbar.jovian.datasource.user.UserDataSourceFactory

/**
 * Created by thein on 1/12/19.
 */
class UsersVM : ViewModel()  {

    private val dataSourceFactory = run { UserDataSourceFactory(apolloClient) }

    val userList = run {
        val pagedListConfig = PagedList.Config.Builder().setInitialLoadSizeHint(10).setPageSize(10).build()
        LivePagedListBuilder(dataSourceFactory, pagedListConfig).build()
    }

    val loadInitial: LiveData<Boolean> = dataSourceFactory.loadingInitial
    val loadBefore: LiveData<Boolean> = dataSourceFactory.loadingBefore
    val loadAfter: LiveData<Boolean> = dataSourceFactory.loadingAfter
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