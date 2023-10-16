package com.finderbar.jovian.fragments.discuss

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.finderbar.jovian.OnCvItemClick
import com.finderbar.jovian.R
import com.finderbar.jovian.activity.UserProfileActivity
import com.finderbar.jovian.adaptor.discuss.VoteAdaptor
import com.finderbar.jovian.databinding.FragmentDiscussVoteBinding
import com.finderbar.jovian.models.VoteType
import com.finderbar.jovian.utilities.AppConstants.ARG_KEY_DISCUSS_ID
import com.finderbar.jovian.utilities.NetworkState
import com.finderbar.jovian.viewmodels.discuss.QuestionUpVoteVM

class QuestionUpVoteFragment: Fragment(), OnCvItemClick {

    private lateinit var questionUpVoteVM: QuestionUpVoteVM
    private lateinit var voteAdaptor: VoteAdaptor
    private var discussId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        questionUpVoteVM = ViewModelProviders.of(this).get(QuestionUpVoteVM::class.java)
        retainInstance = true
        var bundle = savedInstanceState
        if (bundle == null) bundle = arguments
        if (bundle != null) {
            discussId = bundle.getString(ARG_KEY_DISCUSS_ID)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        discussId = arguments?.getString(ARG_KEY_DISCUSS_ID)
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
            questionUpVoteVM.refresh()
        }

        questionUpVoteVM.setId(discussId!!)

        questionUpVoteVM.voteList.observe(this, Observer { pagedList ->
            voteAdaptor.submitList(pagedList)
            binding.swipeRefreshLayout.isRefreshing = false
        })

        questionUpVoteVM.loadingInitial.observe(this, Observer { loading ->
            val isRefreshing = loading == true
            if (binding.swipeRefreshLayout.isRefreshing != isRefreshing) {
                binding.swipeRefreshLayout.isRefreshing = isRefreshing
            }
        })

        questionUpVoteVM.loadingBefore.observe(this, Observer {
            voteAdaptor.updateLoadingBefore(it == true)
        })

        questionUpVoteVM.loadingAfter.observe(this, Observer {
            voteAdaptor.updateLoadingAfter(it == true)
        })

        questionUpVoteVM.networkState.observe(this, Observer {
            voteAdaptor.changeItemWithNetWork(it!!)
        })

        questionUpVoteVM.emptyState.observe(this, Observer {
            if(it!!) {
                binding.emptyLayout.visibility = View.GONE
            } else {
                binding.emptyLayout.visibility = View.VISIBLE
            }
        })

        return myView
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        discussId = arguments?.getString(ARG_KEY_DISCUSS_ID)
    }

    override fun onItemClick(userId: String) {
        val intent = Intent(activity, UserProfileActivity::class.java)
        intent.putExtra("userId", userId)
        startActivity(intent)
    }
}
