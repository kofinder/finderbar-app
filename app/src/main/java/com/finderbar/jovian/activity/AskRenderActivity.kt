package com.finderbar.jovian.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import com.finderbar.jovian.R
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.finderbar.jovian.viewmodels.discuss.AskAnswerVM
import es.dmoral.toasty.Toasty
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.finderbar.jovian.fragments.discuss.QuestionPreviewDialogFragment
import com.finderbar.jovian.models.Question
import com.finderbar.jovian.prefs
import com.finderbar.jovian.utilities.markdown.*
import kotlinx.android.synthetic.main.item_editor_action.*
import mabbas007.tagsedittext.TagsEditText
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*


class AskRenderActivity : AppCompatActivity() {

    private var askAnswerVm: AskAnswerVM? = null
    private var mToolbar: Toolbar? = null
    private var txtTagView: TagsEditText? = null;
    private var txtTitle: EditText? = null;
    private var etPrimaryEditor: EditText? = null;
    private var showErrTag: EditText? = null;

    private lateinit var mAwesomeValidation: AwesomeValidation
    private lateinit var dialog: ACProgressFlower

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ask_render)
        askAnswerVm = ViewModelProviders.of(this).get(AskAnswerVM::class.java)


        mToolbar = findViewById(R.id.toolbar)
        mToolbar!!.title = "ASK"
        setSupportActionBar(mToolbar);
        supportActionBar!!.setDisplayHomeAsUpEnabled(true);

        mAwesomeValidation = AwesomeValidation(ValidationStyle.BASIC);
        AwesomeValidation.disableAutoFocusOnFirstFailure();
        dialog = ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .fadeColor(Color.LTGRAY).build()

        txtTagView = findViewById(R.id.edit_tags_view)
        etPrimaryEditor = findViewById(R.id.txt_primary_editor)
        txtTitle = findViewById(R.id.txt_title)
        showErrTag = findViewById(R.id.show_err_tag)
        txtTagView?.hint = "Enter Tags"

        askAnswerVm?.result?.observe(this, Observer {
            dialog.dismiss()
            Toasty.success(this, it!!.message, Toast.LENGTH_SHORT, true).show();
            val intent = Intent(this@AskRenderActivity, DiscussActivity::class.java)
            intent.putExtra("question", Question(it!!.id, it!!.status) as Serializable)
            startActivity(intent)
            finish()
        })

        askAnswerVm?.errorMessage?.observe(this, Observer {
            dialog.dismiss()
            mAwesomeValidation.clear();
            Toasty.error(this, it!!.message, Toast.LENGTH_SHORT, true).show();
        })

        setupEditor(
            applicationContext,
            etPrimaryEditor!!,
            btBold,
            btItalic,
            btStrikethrough,
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
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_editor, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.preview -> {
                if (validate()) {
                    val currentTime = Calendar.getInstance().timeInMillis
                    val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault())
                    val timeAgo = sdf.format(currentTime)
                    val frag = QuestionPreviewDialogFragment.newInstance(
                            txtTitle?.text.toString(),
                            etPrimaryEditor?.text.toString(),
                            txtTagView?.tags!!, prefs.fullName, prefs.avatar, timeAgo)
                    frag.show(supportFragmentManager, QuestionPreviewDialogFragment.TAG)
                }
            }
            R.id.submit -> {
                if (validate()) {
                    dialog.show()
                    askAnswerVm?.saveQuestion(txtTitle?.text.toString(), etPrimaryEditor?.text.toString(), txtTagView?.tags!!)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun validate(): Boolean {
        return if (txtTitle!!.text.length >= 10 && !txtTitle!!.text.isNullOrBlank()) {
            if(etPrimaryEditor?.text?.length!! < 20 || etPrimaryEditor?.text.isNullOrBlank()) {
                etPrimaryEditor?.error = "Body text must be at least 20 characters"
                false
            } else if(txtTagView?.tags?.isEmpty()!!) {
                showErrTag!!.error = "Tag must be at least one tag"
                false
            } else {
                !false;
            }
        } else {
            txtTitle?.error = "Title text must be at least 10 characters";
            false
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
