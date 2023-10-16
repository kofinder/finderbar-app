package com.finderbar.jovian.datasource.discuss

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.paging.DataSource
import com.apollographql.apollo.ApolloClient
import com.finderbar.jovian.models.Answer
import com.finderbar.jovian.utilities.NetworkState

/**
 * Created by thein on 1/5/19.
 */
class AnswerDataSourceFactory(private val apolloClient: ApolloClient): DataSource.Factory<Int, Answer>() {

    private val lastDataSource = MutableLiveData<AnswerDataSource>()
    val loadingInitial: LiveData<Boolean> = Transformations.switchMap(lastDataSource) { dataSource -> dataSource.loadingInitial }
    val loadingBefore: LiveData<Boolean> = Transformations.switchMap(lastDataSource) { dataSource -> dataSource.loadingBefore }
    val loadingAfter: LiveData<Boolean> = Transformations.switchMap(lastDataSource) { dataSource -> dataSource.loadingAfter }
    val networkState: LiveData<NetworkState> = Transformations.switchMap(lastDataSource) { dataSource -> dataSource.networkState }
    var questionId: String = ""

    override fun create(): DataSource<Int, Answer> {
        return AnswerDataSource(apolloClient, questionId).also { lastDataSource.postValue(it) }
    }

    fun refresh() {
        lastDataSource.value?.invalidate()
    }

}