package com.finderbar.jovian.datasource.discuss

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.paging.DataSource
import com.apollographql.apollo.ApolloClient
import com.finderbar.jovian.models.Discuss
import com.finderbar.jovian.utilities.NetworkState


class DiscussDataSourceFactory(private val apolloClient: ApolloClient): DataSource.Factory<Int, Discuss>() {

    private val lastDataSource = MutableLiveData<DiscussDataSource>()
    val loadingInitial: LiveData<Boolean> = Transformations.switchMap(lastDataSource) { dataSource -> dataSource.loadingInitial }
    val loadingBefore: LiveData<Boolean> = Transformations.switchMap(lastDataSource) { dataSource -> dataSource.loadingBefore }
    val loadingAfter: LiveData<Boolean> = Transformations.switchMap(lastDataSource) { dataSource -> dataSource.loadingAfter }
    val networkState: LiveData<NetworkState> = Transformations.switchMap(lastDataSource) { dataSource -> dataSource.networkState }
    var _id: String = ""

    override fun create(): DataSource<Int, Discuss> {
        return DiscussDataSource(apolloClient, _id).also { lastDataSource.postValue(it) }
    }

    fun refresh() {
        lastDataSource.value?.invalidate()
    }

}