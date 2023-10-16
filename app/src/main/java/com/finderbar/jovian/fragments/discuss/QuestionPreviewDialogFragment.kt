package com.finderbar.jovian.fragments.discuss

import android.net.Uri
import android.os.Bundle
import android.support.design.chip.Chip
import android.support.design.chip.ChipGroup
import android.support.v4.app.DialogFragment
import android.view.*
import android.widget.TextView
import com.finderbar.jovian.R
import com.finderbar.jovian.agoTimeUtil
import com.finderbar.jovian.utilities.AppConstants.ARG_KEY_QUESTION_BODY
import com.finderbar.jovian.utilities.AppConstants.ARG_KEY_QUESTION_TAGS
import com.finderbar.jovian.utilities.AppConstants.ARG_KEY_QUESTION_TITLE
import com.finderbar.jovian.utilities.AppConstants.ARG_KEY_TIME_AGO
import com.finderbar.jovian.utilities.AppConstants.ARG_KEY_USER_AVATAR
import com.finderbar.jovian.utilities.AppConstants.ARG_KEY_USER_NAME
import com.finderbar.jovian.utilities.android.loadAvatar
import com.finderbar.jovian.utilities.markdown.setMarkdown
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*


class QuestionPreviewDialogFragment: DialogFragment() {

    private var txtTitle: TextView? = null;
    private var txtBody: TextView? = null;
    private var txtUserName: TextView? = null;
    private var imgAvatar: CircleImageView? = null;
    private var chipGroup: ChipGroup? = null
    private var txtTimeAgo: TextView? = null

    private var title: String? = null
    private var body: String? = null
    private var tags: List<String> = emptyList<String>()
    private var userName: String? = null
    private var userAvatar: String? = null;
    private var timeAgo: String? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val window = dialog.window
        val layoutParams = window.attributes
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        window.attributes = layoutParams
        window.attributes.gravity = Gravity.BOTTOM;
        window.attributes.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        window.setBackgroundDrawableResource(R.drawable.round_border_white)
        window.setWindowAnimations(R.style.DialogAnimation)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var bundle = savedInstanceState
        if (bundle == null) bundle = arguments
        if (bundle != null) {
            title = bundle.getString(ARG_KEY_QUESTION_TITLE)
            body = bundle.getString(ARG_KEY_QUESTION_BODY)
            tags = bundle.getStringArrayList(ARG_KEY_QUESTION_TAGS)!!
            userName = bundle.getString(ARG_KEY_USER_NAME)
            userAvatar = bundle.getString(ARG_KEY_USER_AVATAR)
            timeAgo = bundle.getString(ARG_KEY_TIME_AGO)
        }
    }


    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        val view =  inflater.inflate(R.layout.fragment_dialog_question_preview, parent, false)
        txtUserName = view.findViewById(R.id.txt_usr_name)
        imgAvatar = view.findViewById(R.id.user_image)
        txtTitle = view.findViewById(R.id.txt_title)
        txtBody = view.findViewById(R.id.body)
        chipGroup = view.findViewById(R.id.chip_group)
        txtTimeAgo = view.findViewById(R.id.txt_ago)

        // set initial value
        txtTitle?.text = title
        txtBody?.setMarkdown(body!!)
        txtUserName?.text = userName
        imgAvatar?.loadAvatar(Uri.parse(userAvatar))
        txtTimeAgo?.text = agoTimeUtil(timeAgo!!)

        repeat(tags.size) {
            var chip = Chip(chipGroup!!.context)
            chip.setChipBackgroundColorResource(R.color.pf_white)
            chip.setChipStrokeColorResource(R.color.pf_green)
            chip.setTextAppearanceResource(R.style.ChipTextStyle)
            chip.chipStrokeWidth = 1f
            chip.chipStartPadding = 20.0f;
            chip.chipEndPadding = 20.0f
            chip.textEndPadding = 0.0f
            chip.textStartPadding = 0.0f
            chip.chipMinHeight = 30.0f
            chip.setPadding(5, 10, 5, 10)
            chip.text = tags[it]
            chip.isCheckable = false
            chipGroup!!.addView(chip);
        }
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        txtUserName = null
        imgAvatar = null
        txtBody = null
    }

    companion object {
        const val TAG = "QuestionPreviewDialogFragment"
        fun newInstance(title: String, body: String, tags: MutableList<String>, userName: String, userAvatar: String, timeAgo: String): QuestionPreviewDialogFragment {
            val fragment = QuestionPreviewDialogFragment()
            val args = Bundle()
            args.putString(ARG_KEY_QUESTION_TITLE, title)
            args.putString(ARG_KEY_QUESTION_BODY, body)
            args.putStringArrayList(ARG_KEY_QUESTION_TAGS, tags as ArrayList<String>?)
            args.putString(ARG_KEY_USER_NAME, userName)
            args.putString(ARG_KEY_USER_AVATAR, userAvatar)
            args.putString(ARG_KEY_TIME_AGO, timeAgo)
            fragment.arguments = args
            return fragment
        }
    }
}