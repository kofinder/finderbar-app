package com.finderbar.jovian.adaptor.discuss

import android.arch.paging.PagedListAdapter
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.finderbar.jovian.*
import com.finderbar.jovian.adaptor.diffutils.DiscussDiffUtilCallBack
import com.finderbar.jovian.databinding.ItemDiscussAnswerBinding
import com.finderbar.jovian.databinding.ItemDiscussQuestionBinding
import com.finderbar.jovian.databinding.ItemLoadingBinding
import com.finderbar.jovian.models.*
import com.finderbar.jovian.utilities.AppConstants.ITEM_ANSWER
import com.finderbar.jovian.utilities.AppConstants.ITEM_QUESTION
import com.finderbar.jovian.utilities.AppConstants.LOADING
import com.finderbar.jovian.utilities.NetworkState
import type.VoteStatus

class DiscussAdaptor(private val voteCallback: DiscussVoteCallback, private val favoriteCallback: DiscussFavoriteCallback,
                     private val discussEditCallback: DiscussEditCallback, private val discussCommentCallBack: DiscussCommentCallBack) :
        PagedListAdapter<Discuss, DiscussAdaptor.ItemViewHolder>(DiscussDiffUtilCallBack()) {

    private var loadingBefore = false
    private var loadingAfter = false

    abstract class ItemViewHolder(root: View) : RecyclerView.ViewHolder(root)

    private class QuestionViewHolder(val binding: ItemDiscussQuestionBinding) : ItemViewHolder(binding.root)
    private class AnswerViewHolder(val binding: ItemDiscussAnswerBinding) : ItemViewHolder(binding.root)
    private class LoadingViewHolder(val binding: ItemLoadingBinding) : ItemViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        when (viewType) {
            ITEM_ANSWER -> {
                val binding = ItemDiscussAnswerBinding.inflate(inflater, parent, false)
                return AnswerViewHolder(binding)
            }
            ITEM_QUESTION -> {
                val binding = ItemDiscussQuestionBinding.inflate(inflater, parent, false)
                return QuestionViewHolder(binding)
            }
            LOADING -> {
                val binding = ItemLoadingBinding.inflate(inflater, parent, false)
                return LoadingViewHolder(binding)
            }
        }
        throw IllegalArgumentException("unknown viewType: $viewType") as Throwable
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ITEM_ANSWER -> {
                val datum = super.getItem(position)
                val view = holder as AnswerViewHolder
                view.binding.discuss = datum
                view.binding.upVoteHelper.text = datum?.upVoteHelper.toString()
                view.binding.downVoteHelper.text = datum?.downVoteHelper.toString()

                view.binding.upVote.setOnClickListener{ voteCallback.onItemClick(holder, datum!!._id, VoteStatus.UP, DiscussType.ANSWER) }
                view.binding.downVote.setOnClickListener{ voteCallback.onItemClick(holder, datum!!._id, VoteStatus.DOWN, DiscussType.ANSWER) }
                view.binding.btnEdit.setOnClickListener{ discussEditCallback.onItemClick(datum!!, DiscussType.ANSWER)}
                view.binding.btnComment.setOnClickListener {discussCommentCallBack.onItemClick(it, datum?._id!!, DiscussType.ANSWER)}

                if(datum?.upVoteHelper!! > 0) {
                    view.binding.upVote.icon.color(ContextCompat.getColor(view.itemView.context, R.color.colorPrimary))
                }
                if(datum?.downVoteHelper!! > 0) {
                    view.binding.downVote.icon.color(ContextCompat.getColor(view.itemView.context, R.color.colorPrimary))
                }
                if(datum?.commentCount!! < 1) {
                    view.binding.txtComment.visibility = View.GONE
                } else {
                    view.binding.txtComment.visibility = View.VISIBLE
                }

                if(datum?.upVoteCount!! < 1) {
                    view.binding.txtUpVote.visibility = View.GONE
                } else {
                    view.binding.txtUpVote.visibility = View.VISIBLE
                }

                if(datum?.downVoteCount!! < 1) {
                    view.binding.txtDownVote.visibility = View.GONE
                } else {
                    view.binding.txtDownVote.visibility = View.VISIBLE
                }
            }
            ITEM_QUESTION -> {
                val view = holder as QuestionViewHolder
                val datum = super.getItem(position)
                view.binding.discuss = datum
                view.binding.upVoteHelper.text = datum?.upVoteHelper.toString()
                view.binding.downVoteHelper.text = datum?.downVoteHelper.toString()
                view.binding.favoriteHelper.text = datum?.favoriteHelper.toString()

                view.binding.upVote.setOnClickListener{ voteCallback.onItemClick(holder, datum!!._id, VoteStatus.UP, DiscussType.QUESTION) }
                view.binding.downVote.setOnClickListener{ voteCallback.onItemClick(holder, datum!!._id, VoteStatus.DOWN, DiscussType.QUESTION) }
                view.binding.favorite.setOnClickListener { favoriteCallback.onItemClick(holder, datum!!._id, DiscussType.QUESTION) }
                view.binding.btnEdit.setOnClickListener{ discussEditCallback.onItemClick(datum!!, DiscussType.QUESTION) }
                view.binding.btnComment.setOnClickListener {discussCommentCallBack.onItemClick(it, datum?._id!!, DiscussType.QUESTION)}

                if(datum?.upVoteHelper!! > 0) {
                    view.binding.upVote.icon.color(ContextCompat.getColor(view.itemView.context, R.color.colorPrimary))
                }
                if(datum?.downVoteHelper!! > 0) {
                    view.binding.downVote.icon.color(ContextCompat.getColor(view.itemView.context, R.color.colorPrimary))
                }
                if(datum?.favoriteHelper!! > 0) {
                    view.binding.favorite.icon.color(ContextCompat.getColor(view.itemView.context, R.color.pf_gold))
                }
                if(datum?.commentCount!! < 1) {
                    view.binding.txtComment.visibility = View.GONE
                } else {
                    view.binding.txtComment.visibility = View.VISIBLE
                }

                if(datum?.upVoteCount!! < 1) {
                    view.binding.txtUpVote.visibility = View.GONE
                } else {
                    view.binding.txtUpVote.visibility = View.VISIBLE
                }

                if(datum?.downVoteCount!! < 1) {
                    view.binding.txtDownVote.visibility = View.GONE
                } else {
                    view.binding.txtDownVote.visibility = View.VISIBLE
                }

                if(datum.answerCount < 1) {
                     view.binding.txtAnswer.visibility = View.GONE
                } else {
                     view.binding.txtAnswer.visibility = View.VISIBLE
                }
            }
            LOADING -> {
                holder as LoadingViewHolder
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (!(loadingBefore && position == 0) && !(loadingAfter && position == itemCount - 1)) {
            val datum = getItem(position);
            return when(datum?.discussType) {
                0 -> ITEM_QUESTION
                1 -> ITEM_ANSWER
                else -> ITEM_ANSWER
            }
        } else {
            LOADING
        }
    }

    override fun getItem(position: Int): Discuss? {
        if ((loadingBefore && position == 0) || (loadingAfter && position == itemCount - 1)) {
            return null
        }
        return if (loadingBefore) {
            super.getItem(position - 1)
        } else {
            super.getItem(position)
        }
    }

    private val discussItemCount: Int get() = super.getItemCount()

    override fun getItemCount(): Int {
        if (loadingBefore && loadingAfter) return discussItemCount + 2
        if (loadingBefore || loadingAfter) return discussItemCount + 1
        return discussItemCount
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
