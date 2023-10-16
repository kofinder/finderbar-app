package com.finderbar.jovian.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import com.finderbar.jovian.ItemPostCallBack
import com.finderbar.jovian.models.ImageItem
import com.finderbar.jovian.databinding.ItemPostPhotoGridBinding

class PhotoGridViewHolder(val binding: ItemPostPhotoGridBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(item: ImageItem, position: Int, mDisplay: Int, mTotal: Int) {
        binding.imageItem = item;
        binding.tvCount.text = (mTotal-mDisplay).toString()
        if(mTotal > mDisplay) {
            if(position  == mDisplay-1) {
                binding.tvCount.visibility = View.VISIBLE
                binding.mImageView.imageAlpha = 72
            } else {
                binding.tvCount.visibility = View.INVISIBLE
                binding.mImageView.imageAlpha = 255
            }
        } else {
            binding.mImageView.imageAlpha = 255
            binding.tvCount.visibility = View.INVISIBLE
        }
    }

    fun setClickListener(clickListener: (Any) -> Unit) {
        binding.mImageView.setOnClickListener(clickListener)
    }
}