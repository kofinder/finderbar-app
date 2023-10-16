package com.finderbar.jovian.viewholder

import com.finderbar.jovian.adaptor.post.PostAdaptor
import com.finderbar.jovian.databinding.ItemPostTextBinding
import com.finderbar.jovian.models.Post

class PostTextViewHolder(val binding: ItemPostTextBinding) : PostAdaptor.ItemViewHolder(binding.root) {
    fun bind(post: Post) {
        binding.post = post
    }
}