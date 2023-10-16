package com.finderbar.jovian.activity

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.finderbar.jovian.R
import com.finderbar.jovian.adaptor.ViewPagerAdapter
import com.finderbar.jovian.fragments.discuss.QuestionCommentFragment
import com.finderbar.jovian.fragments.discuss.QuestionDownVoteFragment
import com.finderbar.jovian.fragments.discuss.QuestionFavoriteFragment
import com.finderbar.jovian.fragments.discuss.QuestionUpVoteFragment
import java.util.logging.Logger

class QuestionActionActivity : AppCompatActivity() {
    companion object {
        var Log = Logger.getLogger(QuestionActionActivity::class.java.name)
    }

    private lateinit var mToolbar: Toolbar
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_action)
        mToolbar = findViewById(R.id.toolbar)
        mToolbar.title = "Comments"
        setSupportActionBar(mToolbar);
        supportActionBar!!.setDisplayHomeAsUpEnabled(true);
        mToolbar.setTitleTextColor(Color.BLACK)
        mToolbar.navigationIcon!!.setColorFilter(resources.getColor(R.color.pf_grey), PorterDuff.Mode.SRC_ATOP)


        tabLayout = findViewById(R.id.tabs)
        viewPager = findViewById(R.id.view_pager)

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager)

        tabLayout.getTabAt(0)!!.setIcon(R.drawable.ic_comment_outline_primary)
        tabLayout.getTabAt(1)!!.setIcon(R.drawable.ic_like_outline_primary)
        tabLayout.getTabAt(2)!!.setIcon(R.drawable.ic_dislike_outline_primary)
        tabLayout.getTabAt(3)!!.setIcon(R.drawable.ic_favorite_outline_primary)

        tabLayout.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when(tab.position) {
                    0 -> { mToolbar.title = "Comments" }
                    1 -> { mToolbar.title = "UpVotes" }
                    2 -> { mToolbar.title = "DownVotes" }
                    3 -> { mToolbar.title = "Favorites" }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun setupViewPager(viewPager: ViewPager) {
        var adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFrag(QuestionCommentFragment(), "")
        adapter.addFrag(QuestionUpVoteFragment(), "")
        adapter.addFrag(QuestionDownVoteFragment(), "")
        adapter.addFrag(QuestionFavoriteFragment(), "")
        viewPager.adapter = adapter
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}