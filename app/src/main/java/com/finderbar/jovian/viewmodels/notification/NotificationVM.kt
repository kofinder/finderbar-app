package com.finderbar.jovian.viewmodels.notification

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import com.finderbar.jovian.apolloClient
import com.finderbar.jovian.utilities.NetworkState
import com.finderbar.jovian.datasource.notification.NotificationDataSourceFactory
import com.finderbar.jovian.prefs

class NotificationVM: ViewModel() {

    private val dataSourceFactory = run {
        NotificationDataSourceFactory(apolloClient, prefs.userId)
    }

    val notificationList = run {
        val pagedListConfig = PagedList.Config.Builder().setEnablePlaceholders(false).setInitialLoadSizeHint(10).setPageSize(10).build()
        LivePagedListBuilder(dataSourceFactory, pagedListConfig).build()
    }

    val loadingInitial: LiveData<Boolean> = dataSourceFactory.loadingInitial
    val loadingBefore: LiveData<Boolean> = dataSourceFactory.loadingBefore
    val loadingAfter: LiveData<Boolean> = dataSourceFactory.loadingAfter
    val networkState: LiveData<NetworkState> = dataSourceFactory.networkState


    fun refresh() {
        dataSourceFactory.refresh()
    }

}