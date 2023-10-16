package com.finderbar.jovian.datasource.notification

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PageKeyedDataSource
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.fetcher.ApolloResponseFetchers
import com.finderbar.jovian.models.*
import com.finderbar.jovian.utilities.NetworkState
import query.AllNotificationsQuery

class NotificationDataSource(private val apolloClient: ApolloClient, private val userId: String): PageKeyedDataSource<Int, Notification>()  {

    val networkState = MutableLiveData<NetworkState>()
    val loadingInitial = MutableLiveData<Boolean>()
    val loadingBefore = MutableLiveData<Boolean>()
    val loadingAfter = MutableLiveData<Boolean>()

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Notification>) {
        load(params.requestedLoadSize, 0, loadingInitial) { result ->
            if (result.hasNext) {
                callback.onResult(result.notiList, null, result.notiList.size)
            } else {
                callback.onResult(result.notiList, null, null)
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Notification>) {
        load(params.requestedLoadSize, params.key, loadingAfter) { result ->
            if (result.hasNext) {
                callback.onResult(result.notiList, params.key + result.notiList.size)
            } else {
                callback.onResult(result.notiList, null)
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Notification>) {
        load(params.requestedLoadSize, params.key, loadingBefore) { result ->
            if (result.hasNext) {
                callback.onResult(result.notiList, params.key - result.notiList.size)
            } else {
                callback.onResult(result.notiList, null)
            }
        }
    }

    private fun load(pageLimit: Int, pageSkip: Int, loadingLiveData: MutableLiveData<Boolean>, processResult: (NotificationResult) -> Unit ) {
        val query = setNotificationQuery(InputCriteria(userId, "", pageLimit, pageSkip))
        loadingLiveData.postValue(true)
        networkState.postValue(NetworkState.FETCHING)
        apolloClient.query(query).responseFetcher(ApolloResponseFetchers.NETWORK_FIRST)
                .enqueue(object : ApolloCall.Callback<AllNotificationsQuery.Data>() {
                    override fun onFailure(e: ApolloException) {
                        networkState.postValue(NetworkState.FAILURE)
                    }

                    override fun onResponse(response: Response<AllNotificationsQuery.Data>) {
                        val error = response.data()?.allNotifications()?.error()
                        if(error !== null) {
                            networkState.postValue(NetworkState.FAILURE)
                        } else {
//                            val result = getNotifications(response.data()?.allNotifications()?.notifications()!!)
//                            val hasNext = response.data()?.allNotifications()?.hasNext();
//                            val totalCount = response.data()?.allNotifications()?.totalCount()
//                            networkState.postValue(NetworkState.SUCCESS)
//                            processResult(NotificationResult(result, hasNext!!, totalCount!!.toLong()))
                        }
                    }
                })
    }


    private fun setNotificationQuery(criteria: InputCriteria) = AllNotificationsQuery.builder().criteria(criteria.get()).build()

//    private fun getNotifications(result: MutableList<AllNotificationsQuery.Notification>) : ArrayList<Notification> {
//        var list = ArrayList<Notification>()
//        result.forEach {
//            list.add(Notification(
//                it._id(),
//                it.detailId(),
//                it.title(),
//                it.body(),
//                it.userName(),
//                it.userAvatar(),
//                it.createdAt(),
//                it.updatedAt()!!
//        ))}
//
//        return list
//    }

}