package com.finderbar.jovian.viewholder

import android.net.Uri
import android.view.View
import com.bumptech.glide.request.RequestOptions
import com.finderbar.jovian.R
import com.finderbar.jovian.adaptor.post.MovieAdaptor
import com.finderbar.jovian.databinding.ItemMovieBinding
import com.finderbar.jovian.models.Movie
import im.ene.toro.ToroPlayer
import im.ene.toro.ToroUtil
import im.ene.toro.exoplayer.ExoPlayerViewHelper
import im.ene.toro.media.PlaybackInfo
import im.ene.toro.widget.Container
import com.finderbar.jovian.utilities.android.GlideApp
import com.finderbar.jovian.App
import com.finderbar.jovian.agoTimeUtil
import im.ene.toro.exoplayer.Playable
import java.lang.String
import java.util.*

class MovieViewHolder(val binding: ItemMovieBinding) : MovieAdaptor.ItemViewHolder(binding.root), ToroPlayer {

    private var helper: ExoPlayerViewHelper? = null
    private var mediaUri: Uri? = null
    private var eventListener: ToroPlayer.EventListener? = null

    init {
        playerView?.visibility = View.VISIBLE
    }

    fun bind(movie: Movie) {
        this.mediaUri = Uri.parse(movie.videoUrl)
        binding.timeAgo.text = agoTimeUtil(movie.createdAt)
        binding.userName.text = movie.userName
        //binding.txtTitle.text = movie.title
        GlideApp.with(itemView)
                .load(movie.userAvatar)
                .placeholder(R.drawable.user_image_placeholder)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.userAvatar)

        GlideApp.with(itemView)
                .load(movie.coverUrl)
                .placeholder(R.drawable.cover)
                .apply(RequestOptions.centerCropTransform())
                .into(binding.coverImage)
    }
    private val listener = object : Playable.DefaultEventListener() {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            super.onPlayerStateChanged(playWhenReady, playbackState)
            binding.playerState.text = String.format(Locale.getDefault(), "STATE: %d・PWR: %s", playbackState, playWhenReady)
        }
    }

    override fun initialize(container: Container, playbackInfo: PlaybackInfo) {
        if (mediaUri == null) throw IllegalStateException("mediaUri is null.")
        if (helper == null) {
            helper = ExoPlayerViewHelper(this, mediaUri!!, null, App.config!!)
            helper?.addEventListener(listener)
            helper?.addPlayerEventListener(eventListener!!)
        }
        helper?.initialize(container, playbackInfo)
    }

    fun setClickListener(clickListener: (Any) -> Unit) {
        playerView.setOnClickListener(clickListener)
        binding.coverImage.setOnClickListener(clickListener)
        binding.userAvatar.setOnClickListener(clickListener)
        binding.txtTitle.setOnClickListener(clickListener)
    }

    fun setEventListener(eventListener: ToroPlayer.EventListener) {
        this.eventListener = eventListener
    }

    override fun getCurrentPlaybackInfo(): PlaybackInfo {
        return if (helper != null) helper!!.latestPlaybackInfo else PlaybackInfo()
    }

    override fun release() {
        if (helper != null) {
            helper?.removeEventListener(listener)
            helper!!.removePlayerEventListener(eventListener)
            helper!!.release()
            helper = null
        }
    }

    override fun isPlaying() = helper?.isPlaying ?: false

    override fun getPlayerView(): View = binding.mPlayer

    override fun pause() = helper!!.pause()

    override fun wantsToPlay(): Boolean = ToroUtil.visibleAreaOffset(this, itemView.parent) >= 0.85

    override fun play() = helper!!.play()

    override fun getPlayerOrder(): Int = adapterPosition
}
