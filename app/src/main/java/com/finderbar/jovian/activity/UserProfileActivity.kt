package com.finderbar.jovian.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.BindingAdapter
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.design.chip.Chip
import android.support.design.chip.ChipGroup
import android.support.v7.app.AppCompatActivity
import com.finderbar.jovian.R
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.finderbar.jovian.adaptor.ViewPagerAdapter
import com.finderbar.jovian.agoTimeUtil
import com.finderbar.jovian.fragments.user.UserProfileAnswerFragment
import com.finderbar.jovian.fragments.user.UserProfileQuestionFragment
import com.finderbar.jovian.prefs
import com.finderbar.jovian.utilities.android.loadAvatar
import com.finderbar.jovian.utilities.markdown.setMarkdown
import com.finderbar.jovian.viewmodels.user.UserProfileVM
import kotlinx.android.synthetic.main.activity_user_profile.*


class UserProfileActivity : AppCompatActivity() {

    companion object {
        @JvmStatic
        @BindingAdapter("thumbImage")
        fun loadImage(view: ImageView, thumbImage: String) {
            view.loadAvatar(Uri.parse(thumbImage))
        }

        @JvmStatic
        @BindingAdapter("timeAgo")
        fun loadTimeAgo(view: TextView, timeAgo: String) {
            view.text = agoTimeUtil(timeAgo)
        }

        @JvmStatic
        @BindingAdapter("markdown")
        fun loadMarkdown(view: TextView, markdown: String) {
            view.setMarkdown(markdown)
        }

        @JvmStatic
        @BindingAdapter("chips")
        fun loadTags(chipGroup: ChipGroup, tags: ArrayList<String>) {
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
                chipGroup.addView(chip);
            }
        }
    }

    private lateinit var mToolbar: Toolbar
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager
    private lateinit var userProfileVM: UserProfileVM
    private lateinit var dialog: ACProgressFlower
    private lateinit var menuItem: MenuItem

    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        val userId = intent.getStringExtra("userId")

        mToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(mToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        viewPager = findViewById(R.id.viewpager)
        tabLayout = findViewById(R.id.sliding_tabs)

        setupViewPager(viewPager)
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.getTabAt(0)!!.setIcon(R.drawable.ic_question)
        tabLayout.getTabAt(1)!!.setIcon(R.drawable.ic_answer)

        dialog = ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .fadeColor(Color.LTGRAY).build()

        userProfileVM = ViewModelProviders.of(this).get(UserProfileVM::class.java)
        userProfileVM.getUserProfile(userId)

        userProfileVM.userProfile!!.observe(this, Observer {
            mToolbar.title = it?.userName
            userImage.loadAvatar(Uri.parse(it?.avatar))
        })

    }


    private fun setupViewPager(viewPager: ViewPager) {
        var adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFrag(UserProfileQuestionFragment(), "Questions")
        adapter.addFrag(UserProfileAnswerFragment(), "Answers")
        viewPager.adapter = adapter
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_user_profile, menu)
        menuItem = menu.findItem(R.id.p_edit)
        val userId = intent.getStringExtra("userId")
        menuItem.isVisible = userId == prefs.userId
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.p_edit ->  { startActivity(Intent(this@UserProfileActivity, UserEditProfileActivity::class.java)) }
        }
        return super.onOptionsItemSelected(item)
    }

}