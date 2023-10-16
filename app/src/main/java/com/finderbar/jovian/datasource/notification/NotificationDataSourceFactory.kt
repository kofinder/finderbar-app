package com.finderbar.jovian.datasource.notification

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.paging.DataSource
import com.apollographql.apollo.ApolloClient
import com.finderbar.jovian.utilities.NetworkState
import com.finderbar.jovian.models.Notification

class NotificationDataSourceFactory(private val apolloClient: ApolloClient, private val userId: String): DataSource.Factory<Int, Notification>() {
    private val lastDataSource = MutableLiveData<NotificationDataSource>()

    val loadingInitial: LiveData<Boolean> = Transformations.switchMap(lastDataSource) { dataSource -> dataSource.loadingInitial }
    val loadingBefore: LiveData<Boolean> = Transformations.switchMap(lastDataSource) { dataSource -> dataSource.loadingBefore }
    val loadingAfter: LiveData<Boolean> = Transformations.switchMap(lastDataSource) { dataSource -> dataSource.loadingAfter }
    val networkState: LiveData<NetworkState> = Transformations.switchMap(lastDataSource) { dataSource -> dataSource.networkState }

    override fun create(): DataSource<Int, Notification> {
        return NotificationDataSource(apolloClient, userId).also { lastDataSource.postValue(it) }
    }

    fun refresh() {
        lastDataSource.value?.invalidate()
    }
}
