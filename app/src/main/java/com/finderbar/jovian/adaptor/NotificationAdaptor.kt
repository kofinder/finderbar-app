package com.finderbar.jovian.adaptor

import android.arch.paging.PagedListAdapter
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.request.RequestOptions
import com.finderbar.jovian.OnItemNotifyClick
import com.finderbar.jovian.R
import com.finderbar.jovian.adaptor.diffutils.NotificationDiffUtilCallBack
import com.finderbar.jovian.agoTimeUtil
import com.finderbar.jovian.databinding.ItemLoadingBinding
import com.finderbar.jovian.databinding.ItemNotificationBinding
import com.finderbar.jovian.models.Notification
import com.finderbar.jovian.utilities.android.GlideApp
import com.finderbar.jovian.utilities.AppConstants.ITEM
import com.finderbar.jovian.utilities.AppConstants.LOADING

class NotificationAdaptor(private var onItemNotifyClick: OnItemNotifyClick) :
        PagedListAdapter<Notification, NotificationAdaptor.ItemViewHolder>(NotificationDiffUtilCallBack()) {

    private var loadingBefore = false
    private var loadingAfter = false

    override fun getItemViewType(position: Int): Int {
        if ((loadingBefore && position == 0) || (loadingAfter && position == itemCount - 1)) {
            return LOADING
        } else {
            return ITEM
        }
    }

    abstract class ItemViewHolder(root: View) : RecyclerView.ViewHolder(root)
    private class NotificationViewHolder(val binding: ItemNotificationBinding) : ItemViewHolder(binding.root)
    private class LoadingViewHolder(val binding: ItemLoadingBinding) : ItemViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        when (viewType) {
            ITEM -> {
                val binding = ItemNotificationBinding.inflate(inflater, parent, false)
                return NotificationViewHolder(binding)
            }
            LOADING -> {
                val binding = ItemLoadingBinding.inflate(inflater, parent, false)
                return LoadingViewHolder(binding)
            }
        }

        throw IllegalArgumentException("unknown viewType: $viewType") as Throwable
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        var datum = Notification()
        if (super.getCurrentList()!!.size > position) {
            datum = super.getItem(position)!!;
        }


        if ((loadingBefore && position == 0) || (loadingAfter && position == itemCount - 1)) {
            return
        }

        (holder as? NotificationViewHolder)?.let { viewHolder ->
            holder.itemView.setOnClickListener{onItemNotifyClick.onItemClick(datum!!)}
            viewHolder.binding.notification = datum
            viewHolder.binding.tAgo.text = agoTimeUtil(datum!!.createdAt)
            GlideApp.with(holder.itemView)
                    .load(datum!!.userAvatar)
                    .placeholder(R.drawable.user_image_placeholder)
                    .apply(RequestOptions.circleCropTransform())
                    .into(viewHolder.binding.usrThumb)
        }
    }


    override fun getItem(position: Int): Notification? {
        if ((loadingBefore && position == 0) || (loadingAfter && position == itemCount - 1)) {
            return null
        }
        if (loadingBefore) {
            return super.getItem(position - 1)
        } else {
            return super.getItem(position)
        }
    }

    private val notItemCount: Int get() = super.getItemCount()

    override fun getItemCount(): Int {
        if (loadingBefore && loadingAfter) return notItemCount + 2
        if (loadingBefore || loadingAfter) return notItemCount + 1
        return notItemCount
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
}