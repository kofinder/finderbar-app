package com.finderbar.jovian.adaptor.post

import android.view.LayoutInflater
import android.view.ViewGroup
import com.finderbar.jovian.ItemPostCallBack
import com.finderbar.jovian.viewholder.PhotoGridViewHolder
import com.finderbar.jovian.models.ImageItem
import com.finderbar.jovian.databinding.ItemPostPhotoGridBinding
import com.finderbar.jovian.models.Post
import com.finderbar.jovian.utilities.assymetric.AGVRecyclerViewAdapter
import com.finderbar.jovian.utilities.assymetric.AsymmetricItem

class PhotoGridAdaptor(
        private val callBack: ItemPostCallBack,
        private val post: Post,
        private val items: List<ImageItem>,
        private val maxDisplay: Int = 4,
        private val totalSize: Int
) : AGVRecyclerViewAdapter<PhotoGridViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoGridViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPostPhotoGridBinding.inflate(inflater, parent, false)
        return PhotoGridViewHolder(binding)
    }

    override fun getItem(position: Int): AsymmetricItem = items[position]

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: PhotoGridViewHolder, position: Int) {
        holder.bind(items[position], position, maxDisplay, totalSize)
        holder.setClickListener{callBack.onItemClick(holder.itemView, position, post)}
    }

}