package com.finderbar.jovian.viewholder

import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.finderbar.jovian.ItemPostCallBack
import com.finderbar.jovian.R
import com.finderbar.jovian.adaptor.post.PhotoGridAdaptor
import com.finderbar.jovian.adaptor.post.PostAdaptor
import com.finderbar.jovian.databinding.ItemPostPhotoBinding
import com.finderbar.jovian.fragments.post.PostTimeLinePhotoFragment
import com.finderbar.jovian.models.PostImage
import com.finderbar.jovian.models.Post
import com.finderbar.jovian.utilities.android.SpacesItemDecoration
import com.finderbar.jovian.utilities.assymetric.AsymmetricRecyclerViewAdapter
import com.finderbar.jovian.utilities.assymetric.Utils
import com.finderbar.jovian.utilities.photo.imageItemPresenterFor


class PostPhotoViewHolder(val binding: ItemPostPhotoBinding) : PostAdaptor.ItemViewHolder(binding.root), ItemPostCallBack {

    fun bind(post: Post) {
        binding.post = post
        val images = getImages(post.postImages);
        val presenter = imageItemPresenterFor(images)
        val adaptor = PhotoGridAdaptor(this, post, presenter.item, presenter.maxDisplayItem, images.size)
        binding.recyclerGridView.setRequestedColumnCount(presenter.requestColumns)
        binding.recyclerGridView.isDebugging = true
        binding.recyclerGridView.isAllowReordering
        binding.recyclerGridView.requestedHorizontalSpacing = Utils.dpToPx(itemView.context, 0.0f)
        val dividerItemDecoration = SpacesItemDecoration(itemView.context.resources.getDimensionPixelSize(R.dimen.recycler_padding))
        binding.recyclerGridView.addItemDecoration(dividerItemDecoration)
        binding.recyclerGridView.adapter = AsymmetricRecyclerViewAdapter(itemView.context, binding.recyclerGridView, adaptor)
    }

    private fun getImages(postImages: List<PostImage>): List<String> {
        val result = ArrayList<String>();
        for (x in postImages.indices) {
            if(x >= 4) break
            result.add(postImages[x].url)
        }

        return  result;
    }

    override fun onItemClick(view: View, position: Int, item: Post) {
        val manager: FragmentManager = (view.context as AppCompatActivity).supportFragmentManager
        val morePhotos = PostTimeLinePhotoFragment.newInstance(position, item)
        morePhotos.show(manager, "TimeLinePhotos")
    }

}
