package com.finderbar.jovian.datasource.tutorial

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.paging.DataSource
import com.apollographql.apollo.ApolloClient
import com.finderbar.jovian.models.Category
import com.finderbar.jovian.utilities.NetworkState

class CategoryDataSourceFactory(private val apolloClient: ApolloClient): DataSource.Factory<Int, Category>() {

    private val lastDataSource = MutableLiveData<CategoryDataSource>()
    val loadingInitial: LiveData<Boolean> = Transformations.switchMap(lastDataSource) { dataSource -> dataSource.loadingInitial }
    val loadingBefore: LiveData<Boolean> = Transformations.switchMap(lastDataSource) { dataSource -> dataSource.loadingBefore }
    val loadingAfter: LiveData<Boolean> = Transformations.switchMap(lastDataSource) { dataSource -> dataSource.loadingAfter }
    val networkState: LiveData<NetworkState> = Transformations.switchMap(lastDataSource) { dataSource -> dataSource.networkState }
    var word: String = ""

    override fun create(): DataSource<Int, Category> {
        return CategoryDataSource(apolloClient, word).also { lastDataSource.postValue(it) }
    }

    fun refresh() {
        lastDataSource.value?.invalidate()
    }

}