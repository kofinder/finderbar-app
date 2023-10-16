package com.finderbar.jovian.fragments.post

import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.finderbar.jovian.ItemTimelineCallBack
import com.finderbar.jovian.R
import com.finderbar.jovian.adaptor.post.PostTimeLinePhotoAdaptor
import com.finderbar.jovian.databinding.FragmentPostTimelinePhotoBinding
import com.finderbar.jovian.fragments.common.BlackBoardDialogFragment
import com.finderbar.jovian.models.PostImage
import com.finderbar.jovian.models.Post
import com.finderbar.jovian.utilities.AppConstants.EXTRA_POST_ITEM
import com.finderbar.jovian.utilities.AppConstants.EXTRA_POST_POSITION
import com.finderbar.jovian.utilities.SnapTopLinearSmoothScroller

class PostTimeLinePhotoFragment: BlackBoardDialogFragment(), ItemTimelineCallBack {

    private var adaptor: PostTimeLinePhotoAdaptor? = null;
    private var item: Post? = null;
    private var windowManager: WindowManager? = null
    private var layoutManager: RecyclerView.LayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        item = arguments?.getParcelable(EXTRA_POST_ITEM);
    }

    override fun onCreateView(inflater: LayoutInflater, vg: ViewGroup?, savedInstanceState: Bundle?): View? {
        var binding: FragmentPostTimelinePhotoBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_post_timeline_photo, vg , false)
        var myView : View  = binding.root
        adaptor = PostTimeLinePhotoAdaptor(this, item?.postImages as MutableList<PostImage>)
        layoutManager = object : LinearLayoutManager(context) {
            override fun smoothScrollToPosition(view: RecyclerView, state: RecyclerView.State?, position: Int) {
                val linearSmoothScroller = SnapTopLinearSmoothScroller(view.context)
                linearSmoothScroller.targetPosition = position
                super.startSmoothScroll(linearSmoothScroller)
            }
        }
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adaptor

        return myView
    }

    companion object {
        fun newInstance(position: Int, result: Post): PostTimeLinePhotoFragment {
            val fragment = PostTimeLinePhotoFragment()
            val args = Bundle()
            args.putInt(EXTRA_POST_POSITION, position)
            args.putParcelable(EXTRA_POST_ITEM, result)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onItemClick(view: View, position: Int) {
        val morePhotos = PostTimeLineFragment.newInstance(position, item!!)
        morePhotos.show(childFragmentManager, "PostTimeLineFragment")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    override fun onDetach() {
        super.onDetach()
        windowManager = null
    }

    override fun onDestroyView() {
        layoutManager = null
        super.onDestroyView()
    }
}