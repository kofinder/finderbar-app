package com.finderbar.jovian.viewmodels.discuss

import android.arch.lifecycle.*
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import com.finderbar.jovian.apolloClient
import com.finderbar.jovian.datasource.discuss.QuestionDataSourceFactory
import com.finderbar.jovian.utilities.NetworkState

/**
 * Created by finder on 12/9/18.
 */
class QuestionVM : ViewModel()  {

    private val dataSourceFactory = run {
        QuestionDataSourceFactory(apolloClient)
    }

    val discussList = run {
        val pagedListConfig = PagedList.Config.Builder().setEnablePlaceholders(false).setInitialLoadSizeHint(10).setPageSize(10).build()
        LivePagedListBuilder(dataSourceFactory, pagedListConfig).build()
    }

    val loadingInitial: LiveData<Boolean> = dataSourceFactory.loadingInitial
    val loadingBefore: LiveData<Boolean> = dataSourceFactory.loadingBefore
    val loadingAfter: LiveData<Boolean> = dataSourceFactory.loadingAfter
    val networkState: LiveData<NetworkState> = dataSourceFactory.networkState

    fun setQuery(word: String) {
        dataSourceFactory.query = word
    }

    fun refresh() {
        dataSourceFactory.refresh()
    }
}
