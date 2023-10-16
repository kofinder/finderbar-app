package com.finderbar.jovian.fragments.discuss

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.DialogFragment
import android.support.v4.view.ViewPager
import android.util.DisplayMetrics
import android.view.*
import com.finderbar.jovian.R
import com.finderbar.jovian.adaptor.ViewPagerAdapter
import com.finderbar.jovian.utilities.AppConstants.ARG_KEY_DISCUSS_ID
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.pixeden_7_stroke_typeface_library.Pixeden7Stroke


class QuestionActionDialogFragment: DialogFragment() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager
    private var discussId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        var bundle = savedInstanceState
        if (bundle == null) bundle = arguments
        if (bundle != null) {
            discussId = bundle.getString(ARG_KEY_DISCUSS_ID)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val window = dialog.window
        val layoutParams = window.attributes
        val displayMetrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels / 1.5
        //val width = displayMetrics.widthPixels

        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        layoutParams.height = height.toInt()
        window.attributes = layoutParams
        window.attributes.gravity = Gravity.BOTTOM
        window.attributes.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        window.setBackgroundDrawableResource(R.drawable.round_border_white)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        window.setWindowAnimations(R.style.DialogAnimation)
    }

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view =  inflater.inflate(R.layout.fragment_dialog_comment, parent, false)
        tabLayout = view.findViewById(R.id.tab_layout)
        viewPager = view.findViewById(R.id.view_pager)

        setupViewPager(viewPager)
        tabLayout.setupWithViewPager(viewPager)

        tabLayout.getTabAt(0)!!.icon = IconicsDrawable(this.context, Pixeden7Stroke.Icon.pe7_7s_comment)
        tabLayout.getTabAt(1)!!.icon = IconicsDrawable(this.context, Pixeden7Stroke.Icon.pe7_7s_like)
        tabLayout.getTabAt(2)!!.icon = IconicsDrawable(this.context, Pixeden7Stroke.Icon.pe7_7s_up_arrow)
        tabLayout.getTabAt(3)!!.icon = IconicsDrawable(this.context, Pixeden7Stroke.Icon.pe7_7s_bottom_arrow)
        return view
    }

    private fun setupViewPager(viewPager: ViewPager) {
        var adapter = ViewPagerAdapter(childFragmentManager)
        adapter.addFrag(QuestionCommentFragment(), "comment", discussId!!)
        adapter.addFrag(QuestionFavoriteFragment(), "like", discussId!!)
        adapter.addFrag(QuestionUpVoteFragment(), "upvote", discussId!!)
        adapter.addFrag(QuestionDownVoteFragment(), "downvote", discussId!!)
        viewPager.adapter = adapter
    }


    companion object {
        const val TAG = "QuestionActionDialogFragment"
        fun newInstance(_id: String): QuestionActionDialogFragment {
            val fragment = QuestionActionDialogFragment()
            val args = Bundle()
            args.putString(ARG_KEY_DISCUSS_ID, _id)
            fragment.arguments = args
            return fragment
        }
    }
}