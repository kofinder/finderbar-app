package com.finderbar.jovian.viewmodels.discuss

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import com.finderbar.jovian.apolloClient
import com.finderbar.jovian.datasource.discuss.AnswerDataSourceFactory
import com.finderbar.jovian.utilities.NetworkState

/**
 * Created by thein on 12/22/18.
 */

class AnswerVM: ViewModel() {

    private var dataSourceFactory = run { AnswerDataSourceFactory(apolloClient) }

    val loadingInitial: LiveData<Boolean> = dataSourceFactory.loadingInitial
    val loadingBefore: LiveData<Boolean> = dataSourceFactory.loadingBefore
    val loadingAfter: LiveData<Boolean> = dataSourceFactory.loadingAfter
    val networkState: LiveData<NetworkState> = dataSourceFactory.networkState

    val answers = run {
        val config = PagedList.Config.Builder().setInitialLoadSizeHint(10).setPageSize(10).build()
        LivePagedListBuilder(dataSourceFactory, config).build()
    }

    fun setId(questionId: String) {
        dataSourceFactory.questionId = questionId
    }

    fun refresh() {
        dataSourceFactory.refresh()
    }

    override fun onCleared() {
        super.onCleared()
    }

}