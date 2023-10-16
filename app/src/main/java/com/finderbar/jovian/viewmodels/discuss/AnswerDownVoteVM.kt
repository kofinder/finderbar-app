package com.finderbar.jovian.viewmodels.discuss

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import com.finderbar.jovian.apolloClient
import com.finderbar.jovian.datasource.discuss.AnswerDownVoteDataSourceFactory
import com.finderbar.jovian.utilities.NetworkState

class AnswerDownVoteVM: ViewModel() {

    private var dataSourceFactory = run { AnswerDownVoteDataSourceFactory(apolloClient) }
    val loadingInitial: LiveData<Boolean> = dataSourceFactory.loadingInitial
    val loadingBefore: LiveData<Boolean> = dataSourceFactory.loadingBefore
    val loadingAfter: LiveData<Boolean> = dataSourceFactory.loadingAfter
    val emptyState: LiveData<Boolean> = dataSourceFactory.emptyState
    val networkState: LiveData<NetworkState> = dataSourceFactory.networkState

    val voteList = run {
        val config = PagedList.Config.Builder().setInitialLoadSizeHint(10).setPageSize(10).build()
        LivePagedListBuilder(dataSourceFactory, config).build()
    }

    fun setId(answerId: String) {
        dataSourceFactory.answerId = answerId
    }

    fun refresh() {
        dataSourceFactory.refresh()
    }

    override fun onCleared() {
        super.onCleared()
    }
}