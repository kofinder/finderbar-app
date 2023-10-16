package com.finderbar.jovian.utilities

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView


abstract class EndlessScrollListener : RecyclerView.OnScrollListener() {
    private var previousTotalItemCount = 0
    private var loading = true
    private val visibleThreshold = 5
    internal var firstVisibleItem: Int = 0
    internal var visibleItemCount: Int = 0
    internal var totalItemCount: Int = 0
    private val startingPageIndex = 0
    private var currentPage = 0

    override fun onScrolled(mRecyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(mRecyclerView, dx, dy)
        val mLayoutManager = mRecyclerView.layoutManager as LinearLayoutManager?
        visibleItemCount = mRecyclerView.childCount
        totalItemCount = mLayoutManager!!.itemCount
        firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition()
        onScroll(firstVisibleItem, visibleItemCount, totalItemCount)
    }

    private fun onScroll(firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
        if (totalItemCount < previousTotalItemCount) {
            this.currentPage = this.startingPageIndex
            this.previousTotalItemCount = totalItemCount
            if (totalItemCount == 0) {
                this.loading = true
            }
        }
        if (loading && totalItemCount > previousTotalItemCount) {
            loading = false
            previousTotalItemCount = totalItemCount
            currentPage++
        }
        if (!loading && totalItemCount - visibleItemCount <= firstVisibleItem + visibleThreshold) {
            onLoadMore(currentPage + 1, totalItemCount)
            loading = true
        }
    }

    abstract fun onLoadMore(page: Int, totalItemsCount: Int)
}