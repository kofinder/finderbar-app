package com.finderbar.jovian.adaptor.post

import android.arch.paging.PagedListAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.finderbar.jovian.OnCompleteCallback
import com.finderbar.jovian.adaptor.diffutils.MovieDiffUtilCallBack
import com.finderbar.jovian.viewholder.TimeLineViewHolder
import com.finderbar.jovian.databinding.ItemMovieTimelineBinding
import com.finderbar.jovian.models.Movie
import im.ene.toro.CacheManager
import im.ene.toro.ToroPlayer

class PostTimeLineMovieAdaptor(private val baseItem: Movie, private val onCompleteCallback: OnCompleteCallback): PagedListAdapter<Movie, TimeLineViewHolder>(MovieDiffUtilCallBack()), CacheManager {

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeLineViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMovieTimelineBinding.inflate(inflater, parent, false)
        val view =  TimeLineViewHolder(binding)
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
            override fun onCompleted() {
                onCompleteCallback.onCompleted(view)
            }
        })

        return view
    }

    override fun onBindViewHolder(holder: TimeLineViewHolder, position: Int): Unit = getItem(position)?.let { holder.bind(it) }!!

    public override fun getItem(position: Int): Movie? {
        if (position == 0) return baseItem
        return super.getItem(position)
    }

    override fun getItemCount(): Int = super.getItemCount()

    override fun getItemId(position: Int): Long  = position.toLong()

    override fun getKeyForOrder(order: Int): Any? = getItem(order)

    override fun getOrderForKey(key: Any): Int? = currentList?.indexOf(key)

    internal fun findNextPlayerPosition(base: Int): Int = base + 1
}
