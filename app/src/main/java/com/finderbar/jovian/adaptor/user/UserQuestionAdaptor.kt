package com.finderbar.jovian.adaptor.user

import android.arch.paging.PagedListAdapter
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.finderbar.jovian.OnCvItemClick
import com.finderbar.jovian.adaptor.diffutils.QuestionDiffUtilCallback
import com.finderbar.jovian.databinding.ItemLoadingBinding
import com.finderbar.jovian.databinding.ItemUserQuestionBinding
import com.finderbar.jovian.models.Question
import com.finderbar.jovian.utilities.AppConstants.ITEM
import com.finderbar.jovian.utilities.AppConstants.LOADING
import com.finderbar.jovian.utilities.NetworkState

class UserQuestionAdaptor(private var onCvItemClick: OnCvItemClick) : PagedListAdapter<Question, UserQuestionAdaptor.ItemViewHolder>(QuestionDiffUtilCallback()) {

    private var loadingBefore = false
    private var loadingAfter = false

    abstract class ItemViewHolder(root: View) : RecyclerView.ViewHolder(root)
    private class QuestionViewHolder(val binding: ItemUserQuestionBinding) : ItemViewHolder(binding.root)
    private class LoadingViewHolder(val binding: ItemLoadingBinding) : ItemViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        when (viewType) {
            ITEM -> {
                val binding = ItemUserQuestionBinding.inflate(inflater, parent, false)
                return QuestionViewHolder(binding)
            }
            LOADING -> {
                val binding = ItemLoadingBinding.inflate(inflater, parent, false)
                return LoadingViewHolder(binding)
            }
        }

        throw IllegalArgumentException("unknown viewType: ${viewType}")
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        var datum = Question()
        if (super.getCurrentList()!!.size > position) {
            datum = super.getItem(position)!!;
        }

        if ((loadingBefore && position == 0) || (loadingAfter && position == itemCount - 1)) {
            return
        }

        (holder as? QuestionViewHolder)?.let { viewHolder ->
            viewHolder.binding.question = datum
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if ((loadingBefore && position == 0) || (loadingAfter && position == itemCount - 1)) {
            LOADING
        } else {
            ITEM
        }
    }

    override fun getItem(position: Int): Question? {
        if ((loadingBefore && position == 0) || (loadingAfter && position == itemCount - 1)) {
            return null
        }
        return if (loadingBefore) {
            super.getItem(position - 1)
        } else {
            super.getItem(position)
        }
    }

    private val queItemCount: Int get() = super.getItemCount()

    override fun getItemCount(): Int {
        if (loadingBefore && loadingAfter) return queItemCount + 2
        if (loadingBefore || loadingAfter) return queItemCount + 1
        return queItemCount
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
