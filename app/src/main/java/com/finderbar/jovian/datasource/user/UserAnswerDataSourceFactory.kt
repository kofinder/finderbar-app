package com.finderbar.jovian.datasource.user

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.paging.DataSource
import com.apollographql.apollo.ApolloClient
import com.finderbar.jovian.models.Answer
import com.finderbar.jovian.utilities.NetworkState

class UserAnswerDataSourceFactory(private val apolloClient: ApolloClient): DataSource.Factory<Int, Answer>() {
    private val lastDataSource = MutableLiveData<UserAnswerDataSource>()

    val loadingInitial: LiveData<Boolean> = Transformations.switchMap(lastDataSource) { dataSource -> dataSource.loadingInitial }
    val loadingBefore: LiveData<Boolean> = Transformations.switchMap(lastDataSource) { dataSource -> dataSource.loadingBefore }
    val loadingAfter: LiveData<Boolean> = Transformations.switchMap(lastDataSource) { dataSource -> dataSource.loadingAfter }
    val networkState: LiveData<NetworkState> = Transformations.switchMap(lastDataSource) { dataSource -> dataSource.networkState }
    var userId = ""

    override fun create(): DataSource<Int, Answer> {
        return UserAnswerDataSource(apolloClient, userId).also {
            lastDataSource.postValue(it)
        }
    }

    fun refresh() {
        lastDataSource.value?.invalidate()
    }
}