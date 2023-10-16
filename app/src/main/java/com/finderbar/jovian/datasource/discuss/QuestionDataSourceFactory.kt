package com.finderbar.jovian.datasource.discuss

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import com.finderbar.jovian.models.Question
import android.arch.paging.DataSource
import com.apollographql.apollo.ApolloClient
import com.finderbar.jovian.utilities.NetworkState

/**
 * Created by finderbar on 12/28/18.
 */

class QuestionDataSourceFactory(private val apolloClient: ApolloClient): DataSource.Factory<Int, Question>() {

    private val lastDataSource = MutableLiveData<QuestionDataSource>()
    val loadingInitial: LiveData<Boolean> = Transformations.switchMap(lastDataSource) { dataSource -> dataSource.loadingInitial }
    val loadingBefore: LiveData<Boolean> = Transformations.switchMap(lastDataSource) { dataSource -> dataSource.loadingBefore }
    val loadingAfter: LiveData<Boolean> = Transformations.switchMap(lastDataSource) { dataSource -> dataSource.loadingAfter }
    val networkState: LiveData<NetworkState> = Transformations.switchMap(lastDataSource) { dataSource -> dataSource.networkState }
    var query = ""

    override fun create(): DataSource<Int, Question> {
        return QuestionDataSource(apolloClient, query).also { lastDataSource.postValue(it) }
    }

    fun refresh() {
        lastDataSource.value?.invalidate()
    }
}