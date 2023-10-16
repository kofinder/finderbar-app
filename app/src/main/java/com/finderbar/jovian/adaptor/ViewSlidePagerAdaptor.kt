package com.finderbar.jovian.adaptor

import android.net.Uri
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.finderbar.jovian.R
import com.finderbar.jovian.models.PostImage
import com.finderbar.jovian.utilities.android.loadLarge
import com.github.chrisbanes.photoview.PhotoView


class ViewSlidePagerAdaptor(private val items: MutableList<PostImage>): PagerAdapter() {

    override fun getCount(): Int = items.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(container.context)
        val itemView: View = inflater.inflate(R.layout.item_post_timeline, container, false)
        val photoView = itemView.findViewById(R.id.iv_photo) as PhotoView
        photoView.loadLarge(Uri.parse(items[position].url))
        container.addView(itemView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        return photoView
    }

    override fun destroyItem(container: ViewGroup, position: Int, v: Any) {
        container.removeView(v as View)
    }

    override fun isViewFromObject(view: View, v: Any) = view === v

}