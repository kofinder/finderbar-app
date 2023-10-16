package com.finderbar.jovian.adaptor.post

import android.arch.paging.PagedListAdapter
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.finderbar.jovian.ItemMovieCallback
import com.finderbar.jovian.adaptor.diffutils.PostDiffUtilCallBack
import com.finderbar.jovian.viewholder.PostMovieViewHolder
import com.finderbar.jovian.viewholder.PostPhotoViewHolder
import com.finderbar.jovian.viewholder.PostTextViewHolder
import com.finderbar.jovian.databinding.ItemLoadingBinding
import com.finderbar.jovian.databinding.ItemPostMovieBinding
import com.finderbar.jovian.databinding.ItemPostPhotoBinding
import com.finderbar.jovian.databinding.ItemPostTextBinding
import com.finderbar.jovian.utilities.NetworkState
import com.finderbar.jovian.models.Post
import com.finderbar.jovian.utilities.AppConstants.LOADING
import com.finderbar.jovian.utilities.AppConstants.TYPE_POST_PHOTO
import com.finderbar.jovian.utilities.AppConstants.TYPE_POST_TEXT
import com.finderbar.jovian.utilities.AppConstants.TYPE_POST_VIDEO
import im.ene.toro.CacheManager
import im.ene.toro.ToroPlayer

class PostAdaptor(private val mvCallback: ItemMovieCallback): PagedListAdapter<Post, PostAdaptor.ItemViewHolder>(PostDiffUtilCallBack()), CacheManager {

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
            TYPE_POST_VIDEO -> {
                val binding = ItemPostMovieBinding.inflate(inflater, parent, false)
                val view = PostMovieViewHolder(binding)
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
                return view;
            }
            TYPE_POST_PHOTO -> {
                val binding = ItemPostPhotoBinding.inflate(inflater, parent, false)
                return PostPhotoViewHolder(binding)
            }
            TYPE_POST_TEXT -> {
                val binding = ItemPostTextBinding.inflate(inflater, parent, false)
                return PostTextViewHolder(binding)
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
            TYPE_POST_VIDEO -> {
                val datum = super.getItem(position)
                val view = holder as PostMovieViewHolder
                view.bind(datum!!)
            }
            TYPE_POST_PHOTO -> {
                val datum = super.getItem(position)
                val view = holder as PostPhotoViewHolder
                view.bind(datum!!)
            }
            TYPE_POST_TEXT -> {
                val datum = super.getItem(position)
                val view = holder as PostTextViewHolder
                view.bind(datum!!)
            }
            LOADING -> {
                holder as LoadingViewHolder
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (!(loadingBefore && position == 0) && !(loadingAfter && position == itemCount - 1)) {
            val datum = getItem(position);
            return when(datum?.postType) {
                TYPE_POST_VIDEO -> TYPE_POST_VIDEO
                TYPE_POST_PHOTO -> TYPE_POST_PHOTO
                else -> TYPE_POST_TEXT
            }
        } else {
            LOADING
        }
    }

    override fun getItem(position: Int): Post? {
        if ((loadingBefore && position == 0) || (loadingAfter && position == itemCount - 1)) {
            return null
        }
        return if (!loadingBefore) {
            super.getItem(position)
        } else {
            super.getItem(position - 1)
        }
    }

    private val pItemCount: Int get() = super.getItemCount()

    override fun getItemCount(): Int {
        if (loadingBefore && loadingAfter) return pItemCount + 2
        if (loadingBefore || loadingAfter) return pItemCount + 1
        return pItemCount
    }

    override fun getKeyForOrder(order: Int): Any?  = getItem(order)

    override fun getOrderForKey(key: Any): Int? = currentList?.indexOf(key)

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