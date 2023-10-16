package com.finderbar.jovian.adaptor.tutorial

import android.arch.paging.PagedListAdapter
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.finderbar.jovian.OnEntityItemClick
import com.finderbar.jovian.adaptor.diffutils.CategoryDiffUtilCallBack
import com.finderbar.jovian.databinding.ItemCategoryBinding
import com.finderbar.jovian.databinding.ItemLoadingBinding
import com.finderbar.jovian.models.Category
import com.finderbar.jovian.utilities.NetworkState
import com.finderbar.jovian.utilities.NetworkState.FETCHING
import com.finderbar.jovian.utilities.NetworkState.FAILURE
import com.finderbar.jovian.utilities.NetworkState.SUCCESS
import com.finderbar.jovian.utilities.AppConstants.ITEM
import com.finderbar.jovian.utilities.AppConstants.LOADING


class CategoryAdaptor(private var onItemClick: OnEntityItemClick) : PagedListAdapter<Category, CategoryAdaptor.ItemViewHolder>(CategoryDiffUtilCallBack()) {

    private var loadingBefore = false
    private var loadingAfter = false

    override fun getItemViewType(position: Int): Int {
        return if ((loadingBefore && position == 0) || (loadingAfter && position == itemCount - 1)) {
            LOADING
        } else {
            ITEM
        }
    }

    abstract class ItemViewHolder(root: View) : RecyclerView.ViewHolder(root)
    private class TutorialViewHolder(val binding: ItemCategoryBinding) : ItemViewHolder(binding.root)
    private class LoadingViewHolder(binding: ItemLoadingBinding) : ItemViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        when (viewType) {
            ITEM -> {
                val binding = ItemCategoryBinding.inflate(inflater, parent, false)
                return TutorialViewHolder(binding)
            }
            LOADING -> {
                val binding = ItemLoadingBinding.inflate(inflater, parent, false)
                return LoadingViewHolder(binding)
            }
        }

        throw IllegalArgumentException("unknown viewType: $viewType")
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ITEM -> {
                val datum = super.getItem(position)!!;
                (holder as? TutorialViewHolder)?.let { viewHolder ->
                    viewHolder.binding.category = datum
                    holder.itemView.setOnClickListener{onItemClick.onItemClick(datum)}
                }
            }
            LOADING -> {
                holder as LoadingViewHolder
            }
        }
    }


    override fun getItem(position: Int): Category? {
        if ((loadingBefore && position == 0) || (loadingAfter && position == itemCount - 1)) {
            return null
        }
        return if (loadingBefore) {
            super.getItem(position - 1)
        } else {
            super.getItem(position)
        }
    }

    private val userItemCount: Int get() = super.getItemCount()

    override fun getItemCount(): Int {
        if (loadingBefore && loadingAfter) return userItemCount + 2
        if (loadingBefore || loadingAfter) return userItemCount + 1
        return userItemCount
    }

    fun updateLoadingBefore(loading: Boolean) {
        val previousLoading = loadingBefore
        loadingBefore = loading
        if (loading) {
            if (previousLoading != loading) {
                notifyItemInserted(0)
            } else {
                notifyItemChanged(0)
            }
        } else {
            if (previousLoading != loading) {
                notifyItemRemoved(0)
            }
        }
    }

    fun updateLoadingAfter(loading: Boolean) {
        val previousLoading = loadingAfter
        loadingAfter = loading
        if (loading) {
            if (previousLoading != loading) {
                notifyItemInserted(itemCount - 1)
            } else {
                notifyItemChanged(itemCount - 1)
            }
        } else {
            if (previousLoading != loading) {
                notifyItemRemoved(itemCount)
            }
        }
    }

    fun changeItemWithNetWork(status: NetworkState) {
        when(status) {
            FETCHING -> {
                loadingAfter = true
                notifyItemChanged(itemCount - 1)
            }
            SUCCESS -> {
                loadingAfter = false
                notifyItemRemoved(itemCount)
            }
            FAILURE -> {
                Log.d("NetworkState ", status.toString())
            }
        }
    }
}
