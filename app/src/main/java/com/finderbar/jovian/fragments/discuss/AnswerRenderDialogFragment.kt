package com.finderbar.jovian.fragments.discuss

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.*
import android.widget.*
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.finderbar.jovian.DiscussDialogListener
import com.finderbar.jovian.R
import com.finderbar.jovian.models.DiscussType
import com.finderbar.jovian.utilities.AppConstants.ARG_KEY_ANSWER_BODY
import com.finderbar.jovian.utilities.AppConstants.ARG_KEY_QUESTION_ID
import com.finderbar.jovian.utilities.AppConstants.ARG_KEY_TIME_AGO
import com.finderbar.jovian.utilities.AppConstants.ARG_KEY_USER_AVATAR
import com.finderbar.jovian.utilities.AppConstants.ARG_KEY_USER_NAME
import com.finderbar.jovian.utilities.markdown.setupEditor
import com.finderbar.jovian.viewmodels.discuss.AskAnswerVM
import es.dmoral.toasty.Toasty


class AnswerRenderDialogFragment : DialogFragment() {

    private var askAnswerVm: AskAnswerVM? = null

    private lateinit var previewButton: Button
    private lateinit var saveButton: Button
    private lateinit var etPrimaryEditor: EditText
    private lateinit var mAwesomeValidation: AwesomeValidation
    private lateinit var progress: ACProgressFlower

    private var questionId: String? = null
    private var body: String? = null
    private var userName: String? = null
    private var userAvatar: String? = null
    private var timeAgo: String? = null

    // markdown editor
    private lateinit var btBold: ImageButton
    private lateinit var btItalic: ImageButton
    private lateinit var btStrike: ImageButton
    private lateinit var btCode: ImageButton
    private lateinit var btLink: ImageButton
    private lateinit var btIndentIncrease: ImageButton
    private lateinit var btQuote: ImageButton
    private lateinit var btBulletList: ImageButton
    private lateinit var btNumberedList: ImageButton
    private lateinit var btTitle1: ImageButton
    private lateinit var btTitle2: ImageButton
    private lateinit var btTitle3: ImageButton
    private lateinit var btTitle4: ImageButton
    private lateinit var btTitle5: ImageButton
    private lateinit var btTitle6: ImageButton
    private lateinit var llTitleOptions: LinearLayout

    // Listener
    private var listener: DiscussDialogListener? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val window = dialog.window
        val layoutParams = window.attributes
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        window.attributes = layoutParams
        window.attributes.gravity = Gravity.BOTTOM;
        window.attributes.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        window.setBackgroundDrawableResource(R.drawable.round_border_white)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        window.setWindowAnimations(R.style.DialogAnimation)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        askAnswerVm = ViewModelProviders.of(this).get(AskAnswerVM::class.java)
        var bundle = savedInstanceState
        if (bundle == null) bundle = arguments
        if (bundle != null) {
            questionId = bundle.getString(ARG_KEY_QUESTION_ID)
            body = bundle.getString(ARG_KEY_ANSWER_BODY)
            userName = bundle.getString(ARG_KEY_USER_NAME)
            userAvatar = bundle.getString(ARG_KEY_USER_AVATAR)
            timeAgo = bundle.getString(ARG_KEY_TIME_AGO)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        val view =  inflater.inflate(R.layout.fragment_dialog_answer_render, parent, false)

        etPrimaryEditor = view.findViewById(R.id.txt_primary_editor)
        btBold = view.findViewById(R.id.btBold)
        btItalic = view.findViewById(R.id.btItalic)
        btStrike = view.findViewById(R.id.btStrikethrough)
        btCode = view.findViewById(R.id.btCode)
        btLink = view.findViewById(R.id.btLink)
        btIndentIncrease = view.findViewById(R.id.btIndentIncrease)
        btQuote = view.findViewById(R.id.btQuote)
        btBulletList = view.findViewById(R.id.btBulletList)
        btNumberedList = view.findViewById(R.id.btNumberedList)
        btTitle1 = view.findViewById(R.id.btTitle1)
        btTitle2 = view.findViewById(R.id.btTitle2)
        btTitle3 = view.findViewById(R.id.btTitle3)
        btTitle4 = view.findViewById(R.id.btTitle4)
        btTitle5 = view.findViewById(R.id.btTitle5)
        btTitle6 = view.findViewById(R.id.btTitle6)
        llTitleOptions  = view.findViewById(R.id.llTitleOptions)

        previewButton = view.findViewById(R.id.btn_preview)
        saveButton = view.findViewById(R.id.btn_save)

        previewButton.setOnClickListener {
            val frag = AnswerPreviewDialogFragment.newInstance(etPrimaryEditor.text.toString(), userName!!, userAvatar!!, timeAgo!!)
            frag.show(childFragmentManager, AnswerPreviewDialogFragment.TAG)
        }
        saveButton.setOnClickListener{
            progress.show()
            askAnswerVm?.saveAnswer(questionId!!, etPrimaryEditor.text.toString())
        }


        mAwesomeValidation = AwesomeValidation(ValidationStyle.BASIC)
        AwesomeValidation.disableAutoFocusOnFirstFailure()
        progress = ACProgressFlower.Builder(this.context)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .fadeColor(Color.LTGRAY).build()

        askAnswerVm?.result?.observe(this, Observer {
            progress.dismiss()
            dialog.dismiss()
            Toasty.success(this.context!!, it!!.message, Toast.LENGTH_SHORT, true).show();
            //val discuss = Discuss(it!!.id, prefs.userId, it!!.message)
            listener?.setData(it!!.id, DiscussType.ANSWER)
        })

        askAnswerVm?.errorMessage?.observe(this, Observer {
            progress.dismiss()
            mAwesomeValidation.clear();
            Toasty.error(this.context!!, it!!.message, Toast.LENGTH_SHORT, true).show();
        })

        setupEditor(
                activity!!,
                etPrimaryEditor,
                btBold,
                btItalic,
                btStrike,
                btCode,
                btLink,
                btIndentIncrease,
                btQuote,
                btBulletList,
                btNumberedList,
                btTitle1,
                btTitle2,
                btTitle3,
                btTitle4,
                btTitle5,
                btTitle6,
                llTitleOptions
        );
        return view;
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            listener = context as DiscussDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString() + " must implement EditNameDialogListener")
        }
    }

    companion object {
        const val TAG = "AnswerRenderDialogFragment"
        fun newInstance(questionId: String, userName: String, userAvatar: String, timeAgo: String): AnswerRenderDialogFragment {
            val fragment = AnswerRenderDialogFragment()
            val args = Bundle()
            args.putString(ARG_KEY_QUESTION_ID, questionId)
            args.putString(ARG_KEY_USER_NAME, userName)
            args.putString(ARG_KEY_USER_AVATAR, userAvatar)
            args.putString(ARG_KEY_TIME_AGO, timeAgo)
            fragment.arguments = args
            return fragment
        }
    }
}