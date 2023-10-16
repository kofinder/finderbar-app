package com.finderbar.jovian.viewmodels.user

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import com.finderbar.jovian.apolloClient
import com.finderbar.jovian.utilities.NetworkState
import com.finderbar.jovian.datasource.user.UserQuestionDataSourceFactory

class UserProfileQuestionVM: ViewModel() {

    private var dataSourceFactory = run { UserQuestionDataSourceFactory(apolloClient) }
    val loadingInitial: LiveData<Boolean> = dataSourceFactory.loadingInitial
    val loadingBefore: LiveData<Boolean> = dataSourceFactory.loadingBefore
    val loadingAfter: LiveData<Boolean> = dataSourceFactory.loadingAfter
    val networkState: LiveData<NetworkState> = dataSourceFactory.networkState

    val usrQuestion = run {
        val config = PagedList.Config.Builder().setInitialLoadSizeHint(10).setPageSize(10).build()
        LivePagedListBuilder(dataSourceFactory, config).build()
    }

    fun setId(userId: String) {
        dataSourceFactory.userId = userId
    }

    fun refresh() {
        dataSourceFactory.refresh()
    }

    override fun onCleared() {
        super.onCleared()
    }
}