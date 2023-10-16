package com.finderbar.jovian.adaptor.post

import android.arch.paging.PagedListAdapter
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.finderbar.jovian.ItemMovieCallback
import com.finderbar.jovian.adaptor.diffutils.MovieDiffUtilCallBack
import com.finderbar.jovian.viewholder.MovieViewHolder
import com.finderbar.jovian.databinding.ItemLoadingBinding
import com.finderbar.jovian.databinding.ItemMovieBinding
import com.finderbar.jovian.models.Movie
import com.finderbar.jovian.utilities.NetworkState
import com.finderbar.jovian.utilities.NetworkState.FETCHING
import com.finderbar.jovian.utilities.NetworkState.FAILURE
import com.finderbar.jovian.utilities.NetworkState.SUCCESS
import com.finderbar.jovian.utilities.AppConstants.ITEM
import com.finderbar.jovian.utilities.AppConstants.LOADING
import im.ene.toro.CacheManager
import im.ene.toro.ToroPlayer

class MovieAdaptor(private val callback: ItemMovieCallback): PagedListAdapter<Movie, MovieAdaptor.ItemViewHolder>(MovieDiffUtilCallBack()), CacheManager {

    private var loadingBefore = false
    private var loadingAfter = false

    init {
        setHasStableIds(true)
    }

    abstract class ItemViewHolder(root: View) : RecyclerView.ViewHolder(root)
    private class LoadingViewHolder(binding: ItemLoadingBinding) : ItemViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        when (viewType) {
            ITEM -> {
                val binding = ItemMovieBinding.inflate(inflater, parent, false)
                val view = MovieViewHolder(binding)
                view.setEventListener(object : ToroPlayer.EventListener {
                    override fun onBuffering() {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.coverImage.visibility = View.VISIBLE
                    }
                    override fun onPlaying() {
                        binding.progressBar.visibility = View.INVISIBLE
                        binding.coverImage.visibility = View.INVISIBLE
                    }
                    override fun onPaused() {
                        binding.coverImage.visibility = View.GONE
                        binding.progressBar.visibility = View.GONE
                    }
                    override fun onCompleted() {}
                })

                return view
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
                val viewHolder = holder as MovieViewHolder
                viewHolder.bind(datum)
                viewHolder.itemView.setOnClickListener {
                    val pos = viewHolder.adapterPosition
                    if (callback != null && pos != RecyclerView.NO_POSITION) {
                        callback.onItemClick(viewHolder, it, datum, pos)
                    }
                }

//                viewHolder.binding.btnShare.setOnClickListener {
//                    if (position != RecyclerView.NO_POSITION) {
//                        itemClickListener.onItemClick(viewHolder.playerView, position, datum, viewHolder.currentPlaybackInfo)
//                    }
//                }

            }
            LOADING -> {
                holder as LoadingViewHolder
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (!(loadingBefore && position == 0) && !(loadingAfter && position == itemCount - 1)) {
            ITEM
        } else {
            LOADING
        }
    }

    public override fun getItem(position: Int): Movie? {
        if ((loadingBefore && position == 0) || (loadingAfter && position == itemCount - 1)) {
            return null
        }
        return if (!loadingBefore) {
            super.getItem(position)
        } else {
            super.getItem(position - 1)
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

    override fun getKeyForOrder(order: Int): Any?  = getItem(order)

    override fun getOrderForKey(key: Any): Int? = currentList?.indexOf(key)
}