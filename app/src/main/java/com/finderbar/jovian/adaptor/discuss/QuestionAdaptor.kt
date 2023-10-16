package com.finderbar.jovian.adaptor.discuss

import android.arch.paging.PagedListAdapter
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.finderbar.jovian.*
import com.finderbar.jovian.adaptor.diffutils.QuestionDiffUtilCallback
import com.finderbar.jovian.databinding.ItemLoadingBinding
import com.finderbar.jovian.databinding.ItemQuestionBinding
import com.finderbar.jovian.models.Question
import com.finderbar.jovian.utilities.NetworkState
import com.finderbar.jovian.utilities.NetworkState.FETCHING
import com.finderbar.jovian.utilities.NetworkState.FAILURE
import com.finderbar.jovian.utilities.NetworkState.SUCCESS

import com.finderbar.jovian.utilities.AppConstants.ITEM
import com.finderbar.jovian.utilities.AppConstants.LOADING

/**
 * Created by FinderBar on 27-Dec-17.
 */
class QuestionAdaptor(private var onCvItemClick: ItemQuestionCallBack) :
        PagedListAdapter<Question, QuestionAdaptor.ItemViewHolder>(QuestionDiffUtilCallback()) {

    private var loadingBefore = false
    private var loadingAfter = false

    override fun getItemViewType(position: Int): Int {
        return if (!(loadingBefore && position == 0) && !(loadingAfter && position == itemCount - 1)) {
            ITEM
        } else {
            LOADING
        }
    }

    abstract class ItemViewHolder(root: View) : RecyclerView.ViewHolder(root)
    private class QuestionViewHolder(val binding: ItemQuestionBinding) : ItemViewHolder(binding.root)
    private class LoadingViewHolder(val binding: ItemLoadingBinding) : ItemViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        when (viewType) {
            ITEM -> {
                val binding = ItemQuestionBinding.inflate(inflater, parent, false)
                return QuestionViewHolder(binding)
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
                (holder as? QuestionViewHolder)?.let { viewHolder ->
                    viewHolder.binding.question = datum
                    holder.itemView.setOnClickListener{onCvItemClick.onItemClick(it, datum, position)}
                    val voteCount = datum.upVoteCount - datum.downVoteCount;
                    if(datum.commentCount < 1) {
                        viewHolder.binding.txtComment.visibility = View.GONE
                    } else {
                        viewHolder.binding.txtComment.visibility = View.VISIBLE
                    }
                    if(voteCount < 1) {
                        viewHolder.binding.txtVote.visibility = View.GONE
                    } else {
                        viewHolder.binding.txtVote.visibility = View.VISIBLE
                    }
                }
            }
            LOADING -> {
                holder as LoadingViewHolder
            }
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

    private val qItemCount: Int get() = super.getItemCount()

    override fun getItemCount(): Int {
        if (loadingBefore && loadingAfter) return qItemCount + 2
        if (loadingBefore || loadingAfter) return qItemCount + 1
        return qItemCount
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

    fun isEmpty(): Boolean {
        return itemCount == 0
    }

}
