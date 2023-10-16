package com.finderbar.jovian.viewholder

import com.bumptech.glide.request.RequestOptions
import com.finderbar.jovian.adaptor.post.MovieRelatedAdaptor
import com.finderbar.jovian.agoTimeUtil
import com.finderbar.jovian.databinding.ItemMovieRelatedBinding
import com.finderbar.jovian.models.Movie
import com.finderbar.jovian.utilities.android.GlideApp
import com.finderbar.jovian.R

class MovieRelatedViewHolder(val binding: ItemMovieRelatedBinding) : MovieRelatedAdaptor.ItemViewHolder(binding.root) {
    fun bind(movie: Movie) {
        GlideApp.with(itemView)
                .load(movie.coverUrl)
                .placeholder(R.drawable.cover)
                .apply(RequestOptions.centerCropTransform())
                .into(binding.coverImage)
        binding.timeAgo.text = agoTimeUtil(movie.createdAt)
    }
}
