package com.finderbar.jovian.adaptor.user

import android.arch.paging.PagedListAdapter
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.request.RequestOptions
import com.finderbar.jovian.R
import com.finderbar.jovian.adaptor.diffutils.UserDiffUtilCallback
import com.finderbar.jovian.databinding.ItemLoadingBinding
import com.finderbar.jovian.databinding.ItemUserBinding
import com.finderbar.jovian.utilities.NetworkState
import com.finderbar.jovian.utilities.NetworkState.FETCHING
import com.finderbar.jovian.utilities.NetworkState.FAILURE
import com.finderbar.jovian.utilities.NetworkState.SUCCESS
import com.finderbar.jovian.models.User
import com.finderbar.jovian.ItemUserClick
import com.finderbar.jovian.utilities.AppConstants.ITEM
import com.finderbar.jovian.utilities.AppConstants.LOADING
import com.finderbar.jovian.utilities.android.GlideApp

/**
 * Created by FinderBar on 27-Dec-17.
 */
class UserAdaptor(private var onCvItemClick: ItemUserClick) : PagedListAdapter<User, UserAdaptor.ItemViewHolder>(UserDiffUtilCallback()) {

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
    private class UserViewHolder(val binding: ItemUserBinding) : ItemViewHolder(binding.root)
    private class LoadingViewHolder(binding: ItemLoadingBinding) : ItemViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        when (viewType) {
            ITEM -> {
                val binding = ItemUserBinding.inflate(inflater, parent, false)
                return UserViewHolder(binding)
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
                var datum = super.getItem(position)!!;
                (holder as? UserViewHolder)?.let { viewHolder ->
                    viewHolder.binding.user = datum
                    holder.itemView.setOnClickListener {
                        datum._id.let { it1 -> onCvItemClick.onItemClick(it1, datum.userName, datum.avatar) }
                    }
                    GlideApp.with(holder.itemView)
                            .load(datum.avatar)
                            .placeholder(R.drawable.user_image_placeholder)
                            .apply(RequestOptions.circleCropTransform())
                            .into(viewHolder.binding.userImage)
                }
            }
            LOADING -> {
                holder as LoadingViewHolder
            }
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
