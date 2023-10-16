package com.finderbar.jovian.viewholder

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewPropertyAnimator
import com.bumptech.glide.request.RequestOptions
import com.finderbar.jovian.R
import com.finderbar.jovian.agoTimeUtil
import com.finderbar.jovian.databinding.ItemMovieTimelineBinding
import com.finderbar.jovian.models.Movie
import com.finderbar.jovian.utilities.android.GlideApp
import im.ene.toro.ToroPlayer
import im.ene.toro.ToroUtil
import im.ene.toro.exoplayer.ExoPlayerViewHelper
import im.ene.toro.exoplayer.Playable
import im.ene.toro.media.PlaybackInfo
import im.ene.toro.widget.Container
import java.lang.String.format
import java.util.Locale.getDefault

class TimeLineViewHolder(val binding: ItemMovieTimelineBinding) : RecyclerView.ViewHolder(binding.root), ToroPlayer {

    private var helper: ExoPlayerViewHelper? = null
    private var mediaUri: Uri? = null
    private var eventListener: ToroPlayer.EventListener? = null
    private var onPlayAnimator: ViewPropertyAnimator? = null
    private var onPauseAnimator: ViewPropertyAnimator? = null
    private var animatorDuration = 300

    private val listener = object : Playable.DefaultEventListener() {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            super.onPlayerStateChanged(playWhenReady, playbackState)
            binding.playerState.text = format(getDefault(), "STATE: %d・PWR: %s", playbackState, playWhenReady)
        }
    }

    fun setEventListener(eventListener: ToroPlayer.EventListener) {
        this.eventListener = eventListener
    }

    init {
        binding.fbVideoPlayer.visibility = View.VISIBLE
        binding.fbVideoPlayer.useController = false
    }

    fun bind(item: Movie) {
        binding.userName.text = item.userName
        binding.timeAgo.text = agoTimeUtil(item.createdAt)
        //binding.txtTitle.text = item.title
        mediaUri = Uri.parse(item.videoUrl)
        GlideApp.with(itemView)
                .load(item.userAvatar)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.userAvatar)
        GlideApp.with(itemView)
                .load(item.coverUrl)
                .placeholder(R.drawable.cover)
                .apply(RequestOptions.centerCropTransform())
                .into(binding.coverImage)
    }

    override fun initialize(container: Container, playbackInfo: PlaybackInfo) {
        if (mediaUri == null) throw IllegalStateException("mediaUri is null.")
        if (helper == null) {
            helper = ExoPlayerViewHelper(this, mediaUri!!)
            helper?.addEventListener(listener)
            helper?.addPlayerEventListener(eventListener!!)
        }
        helper?.initialize(container, playbackInfo)
    }

    override fun getCurrentPlaybackInfo(): PlaybackInfo {
        return if (helper != null) helper!!.latestPlaybackInfo else PlaybackInfo()
    }

    override fun release() {
        if (onPlayAnimator != null) onPlayAnimator!!.cancel()
        if (onPauseAnimator != null) onPauseAnimator!!.cancel()
        onPlayAnimator = null
        onPauseAnimator = null

        if (helper != null) {
            helper!!.removeEventListener(listener)
            helper!!.removePlayerEventListener(eventListener)
            helper!!.release()
            helper = null
        }
    }

    override fun isPlaying() = helper?.isPlaying ?: false

    override fun getPlayerView(): View  = binding.fbVideoPlayer

    override fun pause() = run {
        binding.fbVideoPlayer.useController = false
        if (onPauseAnimator != null) onPauseAnimator!!.cancel()
        onPauseAnimator = binding.overLay.animate().alpha(1.0f).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationCancel(animation: Animator) {
                animation.end()
            }
        }).setDuration(animatorDuration.toLong())
        onPauseAnimator!!.start()
        if (helper != null) helper!!.pause()
    }

    override fun wantsToPlay(): Boolean = ToroUtil.visibleAreaOffset(this, itemView.parent) >= 0.85

    override fun play() = run {
        binding.fbVideoPlayer.useController = true
        if (onPlayAnimator != null) onPlayAnimator!!.cancel()
        onPlayAnimator = binding.overLay.animate().alpha(0.0f).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationCancel(animation: Animator) {
                animation.end()
            }
        }).setDuration(animatorDuration.toLong())
        onPlayAnimator!!.start()
        if (helper != null) helper!!.play()
    }

    override fun getPlayerOrder(): Int = adapterPosition
}