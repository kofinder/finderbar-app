package com.finderbar.jovian.adaptor.discuss

import android.arch.paging.PagedListAdapter
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.finderbar.jovian.OnCvItemClick
import com.finderbar.jovian.adaptor.diffutils.UserDiffUtilCallback
import com.finderbar.jovian.databinding.ItemDiscussVoteBinding
import com.finderbar.jovian.databinding.ItemLoadingBinding
import com.finderbar.jovian.models.User
import com.finderbar.jovian.models.VoteType
import com.finderbar.jovian.utilities.AppConstants.ITEM
import com.finderbar.jovian.utilities.AppConstants.LOADING
import com.finderbar.jovian.utilities.NetworkState

class VoteAdaptor(private val onCvItemClick: OnCvItemClick, private val status: VoteType): PagedListAdapter<User, VoteAdaptor.ItemViewHolder>(UserDiffUtilCallback()) {

    private var loadingBefore = false
    private var loadingAfter = false

    abstract class ItemViewHolder(root: View) : RecyclerView.ViewHolder(root)
    private class VoteUserViewHolder(val binding: ItemDiscussVoteBinding) : ItemViewHolder(binding.root)
    private class LoadingViewHolder(val binding: ItemLoadingBinding) : ItemViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        when (viewType) {
            ITEM -> {
                val binding = ItemDiscussVoteBinding.inflate(inflater, parent, false)
                return VoteUserViewHolder(binding)
            }
            LOADING -> {
                val binding = ItemLoadingBinding.inflate(inflater, parent, false)
                return LoadingViewHolder(binding)
            }
        }

        throw IllegalArgumentException("unknown viewType: $viewType")
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        if ((loadingBefore && position == 0) || (loadingAfter && position == itemCount - 1)) {
            return
        }
        (holder as? VoteUserViewHolder)?.let { viewHolder ->
            val datum = super.getItem(position)
            viewHolder.binding.user = datum
            holder.itemView.setOnClickListener {onCvItemClick.onItemClick(datum!!._id)}
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if ((loadingBefore && position == 0) || (loadingAfter && position == itemCount - 1)) {
            LOADING
        } else {
            ITEM
        }
    }
    override fun getItem(position: Int): User? {
        if ((loadingBefore && position == 0) || (loadingAfter && position == itemCount - 1)) {
            return null
        }
        return if (loadingBefore) {
            super.getItem(position - 1)
        } else {
            super.getItem(position)
        }
    }

    private val voteItemCount: Int get() = super.getItemCount()

    override fun getItemCount(): Int {
        if (loadingBefore && loadingAfter) return voteItemCount + 2
        if (loadingBefore || loadingAfter) return voteItemCount + 1
        return voteItemCount
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
            NetworkState.FETCHING -> {
                loadingAfter = true
                notifyItemChanged(itemCount - 1)
            }
            NetworkState.SUCCESS -> {
                loadingAfter = false
                notifyItemRemoved(itemCount)
            }
            NetworkState.FAILURE -> {
                Log.d("NetworkState ", status.toString())
            }
        }
    }

    fun isEmpty(): Boolean {
        return itemCount == 0
    }
}