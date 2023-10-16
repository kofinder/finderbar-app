package com.finderbar.jovian.adaptor.user

import android.arch.paging.PagedListAdapter
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.finderbar.jovian.adaptor.diffutils.AnswerDiffUtilCallBack
import com.finderbar.jovian.databinding.ItemLoadingBinding
import com.finderbar.jovian.databinding.ItemUserAnswerBinding
import com.finderbar.jovian.models.Answer
import com.finderbar.jovian.utilities.AppConstants.ITEM
import com.finderbar.jovian.utilities.AppConstants.LOADING
import com.finderbar.jovian.utilities.NetworkState

/**
 * Created by FinderBar on 27-Dec-17.
 */
class UserAnswerAdaptor() : PagedListAdapter<Answer, UserAnswerAdaptor.ItemViewHolder>(AnswerDiffUtilCallBack()) {

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
    private class AnswerViewHolder(val binding: ItemUserAnswerBinding) : ItemViewHolder(binding.root)
    private class LoadingViewHolder(val binding: ItemLoadingBinding) : ItemViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        when (viewType) {
            ITEM -> {
                val binding = ItemUserAnswerBinding.inflate(inflater, parent, false)
                return AnswerViewHolder(binding)
            }
            LOADING -> {
                val binding = ItemLoadingBinding.inflate(inflater, parent, false)
                return LoadingViewHolder(binding)
            }
        }

        throw IllegalArgumentException("unknown viewType: ${viewType}")
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        var datum = Answer()
        if (super.getCurrentList()!!.size > position) {
            datum = super.getItem(position)!!;
        }

        if ((loadingBefore && position == 0) || (loadingAfter && position == itemCount - 1)) {
            return
        }

        (holder as? AnswerViewHolder)?.let { viewHolder ->
            viewHolder.binding.answer = datum
        }
    }


    override fun getItem(position: Int): Answer? {
        if ((loadingBefore && position == 0) || (loadingAfter && position == itemCount - 1)) {
            return null
        }
        if (loadingBefore) {
            return super.getItem(position - 1)
        } else {
            return super.getItem(position)
        }
    }

    private val ansItemCount: Int get() = super.getItemCount()

    override fun getItemCount(): Int {
        if (loadingBefore && loadingAfter) return ansItemCount + 2
        if (loadingBefore || loadingAfter) return ansItemCount + 1
        return ansItemCount
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

}
