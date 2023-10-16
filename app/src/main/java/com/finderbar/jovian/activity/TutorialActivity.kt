package com.finderbar.jovian.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.view.MenuItemCompat
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.*
import android.view.animation.AlphaAnimation
import android.widget.*
import cc.cloudist.acplibrary.ACProgressFlower
import com.bumptech.glide.request.RequestOptions
import com.finderbar.jovian.R
import com.finderbar.jovian.adaptor.tutorial.TutorialAdaptor
import com.finderbar.jovian.models.Tutorial
import com.finderbar.jovian.prefs
import com.finderbar.jovian.viewmodels.user.LoginVM
import com.finderbar.jovian.viewmodels.tutorial.TutorialVM
import es.dmoral.toasty.Toasty
import com.finderbar.jovian.fragments.tutorial.TutorialFragment
import com.finderbar.jovian.models.Category
import com.finderbar.jovian.utilities.android.GlideApp
import kotlinx.android.synthetic.main.activity_tutorial.*


class TutorialActivity: AppCompatActivity(), AppBarLayout.OnOffsetChangedListener {

    private var mToolbar: Toolbar? = null
    private var loginVM: LoginVM? = null
    private var tutorialVM: TutorialVM? = null
    private var adaptor: TutorialAdaptor? = null
    private lateinit var dialog: ACProgressFlower
    private lateinit var notifyMenuItem: MenuItem
    private lateinit var badgeView: TextView
    private var listView: ListView? = null
    private var result: MutableList<Tutorial> = ArrayList()
    private var layout: LinearLayout? = null


    private var mTitle: TextView? = null;
    private var mTxtAuther: TextView? = null;
    private var mTxtLang: TextView? = null;
    private var mTitleContainer: LinearLayout? = null;
    private var mAppBarLayout: AppBarLayout? = null;

    private val PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.6f
    private val PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f
    private val ALPHA_ANIMATIONS_DURATION = 200
    private var mIsTheTitleVisible = false
    private var mIsTheTitleContainerVisible = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_tutorial)

        loginVM = ViewModelProviders.of(this).get(LoginVM::class.java)
        tutorialVM = ViewModelProviders.of(this).get(TutorialVM::class.java)
        mToolbar = findViewById(R.id.main_toolbar)

        mAppBarLayout = findViewById(R.id.main_appbar);
        mToolbar!!.inflateMenu(R.menu.other_menu);
        mTitle = findViewById(R.id.main_textview_title);
        mTitleContainer = findViewById(R.id.main_linearlayout_title);
        mTxtAuther = findViewById(R.id.main_txtAuthorName);
        mTxtLang = findViewById(R.id.main_txtLanguage);

        layout = findViewById(R.id.headerProgress)
        listView = findViewById(R.id.list_item)
        adaptor = TutorialAdaptor(this, result)

        val category = intent.getSerializableExtra("category") as? Category
        mTxtAuther!!.text = category!!.authorName
        mTxtLang!!.text = category!!.languageName
        mTitle!!.text = category!!.languageName
        GlideApp.with(this@TutorialActivity)
                .load(Uri.parse(category!!.langPhoto))
                .placeholder(R.drawable.placeholder)
                .apply(RequestOptions.circleCropTransform())
                .into(mImgAvatar)
       tutorialVM!!.setCategoryId(category!!.categoryId)

        result.clear()

        tutorialVM!!.tutorialList!!.observe(this, Observer {
            result.addAll(it!!)
            listView!!.adapter = adaptor
        })

        listView!!.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val tutorial = parent.adapter.getItem(position) as Tutorial;
            val bundle = Bundle();
            bundle.putSerializable("tutorial", tutorial);
            bundle.putString("heading", category.languageName)
            val fm = supportFragmentManager.beginTransaction()
            val fragDialog = TutorialFragment()
            fragDialog.arguments = bundle
            fragDialog.show(fm, TutorialFragment.TAG)
        }

        ViewCompat.setNestedScrollingEnabled(listView!!, true)

        Task().execute()

        mAppBarLayout!!.addOnOffsetChangedListener(this);
        startAlphaAnimation(mTitle!!, 0, View.INVISIBLE);
    }

    override fun onOffsetChanged(layout: AppBarLayout, offset: Int) {
        val maxScroll = layout.totalScrollRange;
        val percentage = Math.abs(offset) / maxScroll;

        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
    }

    private fun handleAlphaOnTitle(percentage: Int) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {
            if(!mIsTheTitleVisible) {
                startAlphaAnimation(mTitle!!, ALPHA_ANIMATIONS_DURATION.toLong(), View.VISIBLE);
                mIsTheTitleVisible = true;
            }
        } else {

            if (mIsTheTitleVisible) {
                startAlphaAnimation(mTitle!!, ALPHA_ANIMATIONS_DURATION.toLong(), View.INVISIBLE);
                mIsTheTitleVisible = false;
            }
        }
    }

    private fun handleToolbarTitleVisibility(percentage: Int) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if(mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer!!, ALPHA_ANIMATIONS_DURATION.toLong(), View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }

        } else {
            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer!!, ALPHA_ANIMATIONS_DURATION.toLong(), View.VISIBLE);
                mIsTheTitleContainerVisible = true;
            }
        }
    }

    private fun startAlphaAnimation(v: View, duration: Long, visibility: Int) {
        var alphaAnimation: AlphaAnimation? = null;
        if(visibility == View.VISIBLE) {
            alphaAnimation = AlphaAnimation(0f, 1f)
        } else {
            alphaAnimation = AlphaAnimation(1f, 0f)
        }
        alphaAnimation.duration = duration
        alphaAnimation.fillAfter = true
        v.startAnimation(alphaAnimation)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.other_menu, menu)
        notifyMenuItem = menu.findItem(R.id.notify)
        val actionView = MenuItemCompat.getActionView(notifyMenuItem)
        badgeView = actionView.findViewById(R.id.badgeCount)
        notifyMenuItem.actionView.setOnClickListener {
            startActivity(Intent(this@TutorialActivity, NotificationActivity::class.java))
        }
        setupBadge()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.exit -> finish()
            R.id.logout -> {
                dialog.show()
                loginVM?.logOutUser(prefs.userId, prefs.authToken)
                loginVM?.logoutMessage?.observe(this, Observer {
                    dialog.dismiss()
                    Toasty.success(this, it!!, Toast.LENGTH_SHORT, true).show()
                    startActivity(Intent(this@TutorialActivity, SignInActivity::class.java))
                    finish()
                })

                loginVM?.errorMessage?.observe(this, Observer {
                    dialog.dismiss()
                    Toasty.error(this, it!!.message, Toast.LENGTH_SHORT, true).show()
                })
            }
        }

        return super.onOptionsItemSelected(item)

    }

    private fun setupBadge() {
        val badgeCount = prefs.menuBadgeCount.toInt()
        if (badgeCount > 0) {
            badgeView.text = prefs.menuBadgeCount
            badgeView.visibility = View.VISIBLE
        } else {
            badgeView.visibility = View.GONE
        }
    }



    inner class Task : AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg params: Void?): Boolean? {
            try {
                Thread.sleep(3000)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return null
        }

        override fun onPreExecute() {
            layout!!.visibility = View.VISIBLE
            listView!!.visibility = View.GONE
            super.onPreExecute()
        }

        override fun onPostExecute(result: Boolean?) {
            layout!!.visibility = View.GONE
            listView!!.visibility = View.VISIBLE
            adaptor!!.notifyDataSetChanged()
            super.onPostExecute(result)
        }
    }

}