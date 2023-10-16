package com.finderbar.jovian.fragments.user

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
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
import com.finderbar.jovian.activity.DiscussActivity
import com.finderbar.jovian.adaptor.user.UserQuestionAdaptor
import com.finderbar.jovian.databinding.FragmentUserQuestionBinding
import com.finderbar.jovian.viewmodels.user.UserProfileQuestionVM

class UserProfileQuestionFragment : Fragment(), OnCvItemClick {

    private lateinit var userProfileQuestionVM: UserProfileQuestionVM
    private lateinit var adaptor: UserQuestionAdaptor

    override fun onItemClick(disussId: String) {
        val intent = Intent(activity, DiscussActivity::class.java)
        intent.putExtra("discussId", disussId)
        startActivity(intent)
        activity!!.finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        userProfileQuestionVM = ViewModelProviders.of(this).get(UserProfileQuestionVM::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val userId = activity!!.intent.getStringExtra("userId")
        var binding: FragmentUserQuestionBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_question, container , false)
        var myView : View  = binding.root

        adaptor = UserQuestionAdaptor(this)
        val dividerItemDecoration = DividerItemDecoration(binding.recyclerView.context, DividerItemDecoration.VERTICAL)
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        binding.recyclerView.addItemDecoration(dividerItemDecoration)
        binding.recyclerView.setHasFixedSize(false)
        binding.swipeRefreshLayout.isRefreshing = false
        binding.recyclerView.adapter = adaptor

        binding.swipeRefreshLayout.setOnRefreshListener {
            userProfileQuestionVM.refresh()
        }

        userProfileQuestionVM.setId(userId)

        userProfileQuestionVM.usrQuestion.observe(this, Observer { pagedList ->
            adaptor.submitList(pagedList)
            binding.swipeRefreshLayout.isRefreshing = false
        })

        userProfileQuestionVM.loadingInitial.observe(this, Observer { loading ->
            val isRefreshing = loading == true
            if (binding.swipeRefreshLayout.isRefreshing != isRefreshing) {
                binding.swipeRefreshLayout.isRefreshing = isRefreshing
            }
        })

        userProfileQuestionVM.loadingBefore.observe(this, Observer {
            adaptor.updateLoadingBefore(it == true)
        })

        userProfileQuestionVM.loadingAfter.observe(this, Observer {
            adaptor.updateLoadingAfter(it == true)
        })

        return myView
    }
}