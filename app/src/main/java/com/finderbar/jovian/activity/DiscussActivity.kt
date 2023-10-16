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
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.finderbar.jovian.*
import com.finderbar.jovian.adaptor.discuss.DiscussAdaptor
import com.finderbar.jovian.fragments.discuss.*
import com.finderbar.jovian.models.*
import com.finderbar.jovian.utilities.android.loadAvatar
import com.finderbar.jovian.utilities.markdown.setMarkdown
import com.finderbar.jovian.viewmodels.discuss.DiscussVM
import com.finderbar.jovian.viewmodels.discuss.DiscussVoteVM
import com.melnykov.fab.FloatingActionButton
import es.dmoral.toasty.Toasty
import type.VoteStatus
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by FinderBar on 01-Jan-18.
 */
class DiscussActivity : AppCompatActivity(),
        DiscussVoteCallback, DiscussFavoriteCallback,
        DiscussEditCallback, DiscussCommentCallBack,
        DiscussDialogListener {

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
        fun loadTags(chipGroup: ChipGroup, chips: List<String>) {
            chipGroup.removeAllViews()
            repeat(chips.size) {
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
                chip.text = chips[it]
                chip.isCheckable = false
                chipGroup.addView(chip);
            }
        }
    }

    private var item : Question? = null
    private lateinit var discussVM: DiscussVM
    private lateinit var voteVM: DiscussVoteVM
    private lateinit var dialog: ACProgressFlower
    private lateinit var toolbar: Toolbar
    private lateinit var recyclerView: RecyclerView
    private lateinit var fab: FloatingActionButton
    private lateinit var adaptor : DiscussAdaptor
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discuss);

        discussVM = ViewModelProviders.of(this).get(DiscussVM::class.java)
        voteVM = ViewModelProviders.of(this).get(DiscussVoteVM::class.java)
        item = intent.extras.get("question") as Question?

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar);
        supportActionBar!!.setDisplayHomeAsUpEnabled(true);
        supportActionBar!!.title = item?.titleText

        recyclerView = findViewById(R.id.recycler_view)
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout)
        fab = findViewById(R.id.fab)

        dialog = ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .fadeColor(Color.LTGRAY).build()

        recyclerView!!.layoutManager = LinearLayoutManager(this)
        adaptor = DiscussAdaptor(this, this, this, this)
        recyclerView!!.adapter = adaptor
        recyclerView!!.setHasFixedSize(true)

        discussVM.setId(item!!._id)

        swipeRefreshLayout.setOnRefreshListener {
            discussVM.refresh()
        }

        discussVM.discuss.observe(this, Observer { pagedList ->
            swipeRefreshLayout.isRefreshing = false;
            adaptor.submitList(pagedList)
        })

        discussVM.loadingInitial.observe(this, Observer { loading ->
            val isRefreshing = loading == true
            if (swipeRefreshLayout.isRefreshing != isRefreshing) {
                swipeRefreshLayout.isRefreshing = isRefreshing
            }
        })

        discussVM.loadingBefore.observe(this, Observer {
            adaptor.updateLoadingBefore(it == true)
        })

        discussVM.loadingAfter.observe(this, Observer {
            adaptor.updateLoadingAfter(it == true)
        })

        discussVM.networkState.observe(this, Observer {
            adaptor.changeItemWithNetWork(it!!)
        })

        voteVM.errorMessage.observe(this, Observer {
            dialog.dismiss()
            Toasty.error(this, it!!.message, Toast.LENGTH_SHORT, true).show();
        })

        voteVM.result.observe(this, Observer {
            dialog.dismiss()
            Toasty.success(this, it!!.message, Toast.LENGTH_SHORT, true).show();
        })


        fab.attachToRecyclerView(recyclerView)
        fab.setOnClickListener {
            val frag = AnswerRenderDialogFragment.newInstance(item?._id!!, prefs.fullName, prefs.avatar, getCurrentTime())
            frag.show(supportFragmentManager, AnswerRenderDialogFragment.TAG)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        print("requestCode $requestCode")
        print("resultCode $resultCode")
        print("data $data")
    }

    override fun onItemClick(viewHolder: DiscussAdaptor.ItemViewHolder, _id: String, status: VoteStatus, voteType: DiscussType) {
        if(voteType == DiscussType.QUESTION) {
            voteVM.questionVote(viewHolder.itemView, _id, InputVote(prefs.userId, status))
            dialog.show()
        } else {
            voteVM.answerVote(viewHolder.itemView, _id, InputVote(prefs.userId, status))
            dialog.show()
        }
    }

    override fun onItemClick(viewHolder: DiscussAdaptor.ItemViewHolder, _id: String, voteType: DiscussType) {
        if(voteType == DiscussType.QUESTION) {
            voteVM.questionFavorite(viewHolder.itemView, _id, prefs.userId)
            dialog.show()
        } else {
//            voteVM.answerFavorite(viewHolder.itemView, _id, prefs.userId)
//            dialog.show()
        }
    }


    override fun onItemClick(discuss: Discuss, voteType: DiscussType) {
        if(voteType == DiscussType.QUESTION) {
            val frag = QuestionEditDialogFragment.newInstance(discuss)
            frag.show(supportFragmentManager, QuestionEditDialogFragment.TAG)
        } else {
            val frag = AnswerEditDialogFragment.newInstance(item!!._id, discuss)
            frag.show(supportFragmentManager, AnswerEditDialogFragment.TAG)
        }
    }

    private fun getCurrentTime(): String {
        val currentTime = Calendar.getInstance().timeInMillis
        val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault())
        return sdf.format(currentTime)
    }

    override fun onItemClick(view: View, _id: String, type: DiscussType) {
        if(type == DiscussType.QUESTION) {
            val frag = QuestionActionDialogFragment.newInstance(_id)
            frag.show(supportFragmentManager, QuestionActionDialogFragment.TAG)
        } else {
            val frag = AnswerActionDialogFragment.newInstance(_id)
            frag.show(supportFragmentManager, AnswerActionDialogFragment.TAG)
        }
    }

    override fun setData(_id: String, type: DiscussType) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
