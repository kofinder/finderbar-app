package com.finderbar.jovian.fragments.discuss

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.*
import android.widget.*
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.finderbar.jovian.R
import com.finderbar.jovian.models.Discuss
import com.finderbar.jovian.utilities.AppConstants.ARG_KEY_QUESTION_BODY
import com.finderbar.jovian.utilities.AppConstants.ARG_KEY_DISCUSS_ID
import com.finderbar.jovian.utilities.AppConstants.ARG_KEY_QUESTION_TAGS
import com.finderbar.jovian.utilities.AppConstants.ARG_KEY_QUESTION_TITLE
import com.finderbar.jovian.utilities.AppConstants.ARG_KEY_TIME_AGO
import com.finderbar.jovian.utilities.AppConstants.ARG_KEY_USER_AVATAR
import com.finderbar.jovian.utilities.AppConstants.ARG_KEY_USER_NAME
import com.finderbar.jovian.utilities.markdown.setupEditor
import com.finderbar.jovian.viewmodels.discuss.AskAnswerVM
import es.dmoral.toasty.Toasty
import mabbas007.tagsedittext.TagsEditText
import kotlin.collections.ArrayList

class QuestionEditDialogFragment : DialogFragment() {

    private var askAnswerVm: AskAnswerVM? = null

    private lateinit var previewButton: Button
    private lateinit var saveButton: Button

    private lateinit var txtTitle: EditText
    private lateinit var txtTagView: TagsEditText
    private lateinit var etPrimaryEditor: EditText
    private lateinit var showErrTag: EditText

    private lateinit var mAwesomeValidation: AwesomeValidation
    private lateinit var progress: ACProgressFlower

    private var discussId: String? = null
    private var title: String? = null
    private var body: String? = null
    private var tags: List<String> = emptyList<String>()
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val window = dialog.window
        val layoutParams = window.attributes
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        window.attributes = layoutParams
        window.attributes.gravity = Gravity.BOTTOM;
        window.attributes.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        window.setBackgroundDrawableResource(R.drawable.round_border_white)
        window.setWindowAnimations(R.style.DialogAnimation)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        askAnswerVm = ViewModelProviders.of(this).get(AskAnswerVM::class.java)
        var bundle = savedInstanceState
        if (bundle == null) bundle = arguments
        if (bundle != null) {
            discussId = bundle.getString(ARG_KEY_DISCUSS_ID)
            title = bundle.getString(ARG_KEY_QUESTION_TITLE)
            body = bundle.getString(ARG_KEY_QUESTION_BODY)
            tags = bundle.getStringArrayList(ARG_KEY_QUESTION_TAGS)
            userName = bundle.getString(ARG_KEY_USER_NAME)
            userAvatar = bundle.getString(ARG_KEY_USER_AVATAR)
            timeAgo = bundle.getString(ARG_KEY_TIME_AGO)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        val view =  inflater.inflate(R.layout.fragment_dialog_question_edit, parent, false)
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
            val frag = QuestionPreviewDialogFragment.newInstance(txtTitle.text.toString(),
                    etPrimaryEditor.text.toString(), txtTagView.tags, userName!!, userAvatar!!, timeAgo!!)
            frag.show(childFragmentManager, QuestionPreviewDialogFragment.TAG)
        }
        saveButton.setOnClickListener{
            progress.show()
            askAnswerVm?.editQuestion(discussId!!, txtTitle.text.toString(), etPrimaryEditor.text.toString(), txtTagView.tags)
        }

        txtTagView = view.findViewById(R.id.edit_tags_view)
        etPrimaryEditor = view.findViewById(R.id.txt_primary_editor)
        txtTitle = view.findViewById(R.id.txt_title)
        showErrTag = view.findViewById(R.id.show_err_tag)

        txtTitle.setText(title)
        etPrimaryEditor.setText(body)
        txtTagView.setTags(tags.toTypedArray())

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

    companion object {
        const val TAG = "QuestionEditDialogFragment"
        fun newInstance(discuss: Discuss): QuestionEditDialogFragment {
            val fragment = QuestionEditDialogFragment()
            val args = Bundle()
            args.putString(ARG_KEY_DISCUSS_ID, discuss._id)
            args.putString(ARG_KEY_QUESTION_TITLE, discuss.titleText)
            args.putString(ARG_KEY_QUESTION_BODY, discuss.body)
            args.putStringArrayList(ARG_KEY_QUESTION_TAGS, ArrayList(discuss.tagIds))
            args.putString(ARG_KEY_USER_NAME, discuss.userName)
            args.putString(ARG_KEY_USER_AVATAR, discuss.userAvatar)
            args.putString(ARG_KEY_TIME_AGO, discuss.createdAt)
            fragment.arguments = args
            return fragment
        }
    }
}