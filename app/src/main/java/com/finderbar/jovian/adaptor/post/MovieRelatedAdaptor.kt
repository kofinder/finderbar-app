package com.finderbar.jovian.adaptor.post

import android.arch.paging.PagedListAdapter
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.finderbar.jovian.ItemMovieClick
import com.finderbar.jovian.adaptor.diffutils.MovieDiffUtilCallBack
import com.finderbar.jovian.viewholder.MovieRelatedViewHolder
import com.finderbar.jovian.databinding.ItemMovieRelatedBinding
import com.finderbar.jovian.models.Movie
import com.finderbar.jovian.utilities.AppConstants.ITEM
import com.finderbar.jovian.utilities.AppConstants.LOADING

class MovieRelatedAdaptor (private val itemClickListener: ItemMovieClick): PagedListAdapter<Movie, MovieRelatedAdaptor.ItemViewHolder>(MovieDiffUtilCallBack()) {

    private var loadingBefore = false
    private var loadingAfter = false


    abstract class ItemViewHolder(root: View) : RecyclerView.ViewHolder(root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder = MovieRelatedViewHolder(ItemMovieRelatedBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val viewHolder = holder as MovieRelatedViewHolder
        viewHolder.bind(super.getItem(position)!!)
        viewHolder.itemView.setOnClickListener{
            if (itemClickListener != null && position != RecyclerView.NO_POSITION) {
                itemClickListener.onItemClick(it, position, super.getItem(position)!!)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if ((loadingBefore && position == 0) || (loadingAfter && position == itemCount - 1)) {
            return LOADING
        } else {
            return ITEM
        }
    }

    override fun getItem(position: Int): Movie? {
        if ((loadingBefore && position == 0) || (loadingAfter && position == itemCount - 1)) {
            return null
        }
        if (loadingBefore) {
            return super.getItem(position - 1)
        } else {
            return super.getItem(position)
        }
    }

    private val movieItemCount: Int get() = super.getItemCount()

    override fun getItemCount(): Int {
        if (loadingBefore && loadingAfter) return movieItemCount + 2
        if (loadingBefore || loadingAfter) return movieItemCount + 1
        return movieItemCount
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