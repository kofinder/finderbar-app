package com.finderbar.jovian.fragments.user

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
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
import com.finderbar.jovian.adaptor.user.UserAnswerAdaptor
import com.finderbar.jovian.databinding.FragmentUserAnswerBinding
import com.finderbar.jovian.viewmodels.user.UserProfileAnswerVM

class UserProfileAnswerFragment : Fragment(), OnCvItemClick {

    private lateinit var userProfileAnswerVM: UserProfileAnswerVM
    private lateinit var adaptor: UserAnswerAdaptor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        userProfileAnswerVM = ViewModelProviders.of(this).get(UserProfileAnswerVM::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val userId = activity!!.intent.getStringExtra("userId")
        var binding: FragmentUserAnswerBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_answer, container , false)
        var myView : View  = binding.root

        adaptor = UserAnswerAdaptor()
        val dividerItemDecoration = DividerItemDecoration(binding.recyclerView.context, DividerItemDecoration.VERTICAL)
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        binding.recyclerView.addItemDecoration(dividerItemDecoration)
        binding.recyclerView.setHasFixedSize(false)
        binding.swipeRefreshLayout.isRefreshing = false
        binding.recyclerView.adapter = adaptor

        binding.swipeRefreshLayout.setOnRefreshListener {
            userProfileAnswerVM.refresh()
        }

        userProfileAnswerVM.setId(userId)

        userProfileAnswerVM.usrAnswers.observe(this, Observer { pagedList ->
            adaptor.submitList(pagedList)
            binding.swipeRefreshLayout.isRefreshing = false
        })

        userProfileAnswerVM.loadingInitial.observe(this, Observer { loading ->
            val isRefreshing = loading == true
            if (binding.swipeRefreshLayout.isRefreshing != isRefreshing) {
                binding.swipeRefreshLayout.isRefreshing = isRefreshing
            }
        })

        userProfileAnswerVM.loadingBefore.observe(this, Observer {
            adaptor.updateLoadingBefore(it == true)
        })

        userProfileAnswerVM.loadingAfter.observe(this, Observer {
            adaptor.updateLoadingAfter(it == true)
        })

        return myView
    }

    override fun onItemClick(_id: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}