package com.finderbar.jovian.fragments.discuss

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.ViewGroup
import com.finderbar.jovian.OnCvItemClick
import com.finderbar.jovian.R
import com.finderbar.jovian.activity.UserProfileActivity
import com.finderbar.jovian.adaptor.discuss.VoteAdaptor
import com.finderbar.jovian.databinding.FragmentDiscussVoteBinding
import com.finderbar.jovian.models.VoteType
import com.finderbar.jovian.utilities.AppConstants
import com.finderbar.jovian.viewmodels.discuss.AnswerUpVoteVM
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.pixeden_7_stroke_typeface_library.Pixeden7Stroke

class AnswerUpVoteFragment: Fragment(), OnCvItemClick {

    private lateinit var answerUpVoteVM: AnswerUpVoteVM
    private lateinit var voteAdaptor: VoteAdaptor
    private var discussId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        answerUpVoteVM = ViewModelProviders.of(this).get(AnswerUpVoteVM::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        discussId = arguments?.getString(AppConstants.ARG_KEY_DISCUSS_ID)
        var binding: FragmentDiscussVoteBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_discuss_vote, container , false)
        var myView : View  = binding.root

        voteAdaptor = VoteAdaptor(this, VoteType.UP)
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        binding.desText.text = resources.getText(R.string.vote_empty_text)

        val dividerItemDecoration = DividerItemDecoration(binding.recyclerView.context, DividerItemDecoration.VERTICAL)
        binding.recyclerView.addItemDecoration(dividerItemDecoration)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = voteAdaptor

        binding.swipeRefreshLayout.setOnRefreshListener {
            answerUpVoteVM.refresh()
        }

        answerUpVoteVM.setId(discussId!!)

        answerUpVoteVM.voteList.observe(this, Observer { pagedList ->
            voteAdaptor.submitList(pagedList)
            binding.swipeRefreshLayout.isRefreshing = false
        })

        answerUpVoteVM.loadingInitial.observe(this, Observer { loading ->
            val isRefreshing = loading == true
            if (binding.swipeRefreshLayout.isRefreshing != isRefreshing) {
                binding.swipeRefreshLayout.isRefreshing = isRefreshing
            }
        })

        answerUpVoteVM.loadingBefore.observe(this, Observer {
            voteAdaptor.updateLoadingBefore(it == true)
        })

        answerUpVoteVM.loadingAfter.observe(this, Observer {
            voteAdaptor.updateLoadingAfter(it == true)
        })

        answerUpVoteVM.networkState.observe(this, Observer {
            voteAdaptor.changeItemWithNetWork(it!!)
        })

        answerUpVoteVM.emptyState.observe(this, Observer {
            if(it!!) {
                binding.recyclerView.visibility = View.VISIBLE
                binding.emptyLayout.visibility = View.GONE
            } else {
                binding.recyclerView.visibility = View.GONE
                binding.emptyLayout.visibility = View.VISIBLE
            }
        })

        return myView
    }

    override fun onItemClick(userId: String) {
        val intent = Intent(activity, UserProfileActivity::class.java)
        intent.putExtra("userId", userId)
        startActivity(intent)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        discussId = arguments?.getString(AppConstants.ARG_KEY_DISCUSS_ID)
    }
}