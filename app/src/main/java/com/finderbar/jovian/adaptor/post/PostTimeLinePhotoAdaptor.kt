package com.finderbar.jovian.adaptor.post
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.finderbar.jovian.ItemTimelineCallBack
import com.finderbar.jovian.databinding.ItemPostTimelinePhotoBinding
import com.finderbar.jovian.models.PostImage

class PostTimeLinePhotoAdaptor(private val callBack: ItemTimelineCallBack, private val postImages: MutableList<PostImage>): RecyclerView.Adapter<PostTimeLinePhotoAdaptor.ItemViewHolder>() {

    abstract class ItemViewHolder(root: View) : RecyclerView.ViewHolder(root)
    private class PostTimeLinePhotoViewHolder(val binding: ItemPostTimelinePhotoBinding) : ItemViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return PostTimeLinePhotoViewHolder(ItemPostTimelinePhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = postImages.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val view = holder as PostTimeLinePhotoViewHolder
        view.binding.postImage = postImages[position]
        view.itemView.setOnClickListener {
            callBack.onItemClick(it, position)
        }
    }
}