package com.finderbar.jovian.fragments.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.finderbar.jovian.R
import com.finderbar.jovian.adaptor.HackyViewPager
import com.finderbar.jovian.adaptor.ViewSlidePagerAdaptor
import com.finderbar.jovian.fragments.common.BlackBoardDialogFragment
import com.finderbar.jovian.models.Post
import com.finderbar.jovian.models.PostImage
import com.finderbar.jovian.utilities.AppConstants.EXTRA_POST_ITEM
import com.finderbar.jovian.utilities.AppConstants.EXTRA_POST_POSITION


class PostTimeLineFragment : BlackBoardDialogFragment() {

    private var item: Post? = null;
    private var currentItem: Int = 0;
    private var viewPager: HackyViewPager? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        item = arguments?.getParcelable(EXTRA_POST_ITEM);
        currentItem = arguments?.getInt(EXTRA_POST_POSITION)!!
    }

    override fun onCreateView(inflater: LayoutInflater, vg: ViewGroup?, savedInstanceState: Bundle?): View? {
        val myView = inflater.inflate(R.layout.fragment_post_timeline, vg, false)
        viewPager = myView.findViewById(R.id.view_pager)
        viewPager?.adapter = ViewSlidePagerAdaptor(item?.postImages as MutableList<PostImage>)
        viewPager?.setCurrentItem(currentItem, true)
        return myView
    }

    companion object {
        fun newInstance(position: Int, result: Post): PostTimeLineFragment {
            val fragment = PostTimeLineFragment()
            val args = Bundle()
            args.putInt(EXTRA_POST_POSITION, position)
            args.putParcelable(EXTRA_POST_ITEM, result)
            fragment.arguments = args
            return fragment
        }
    }
}