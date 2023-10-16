package com.finderbar.jovian.fragments.user

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.finderbar.jovian.ItemUserClick
import com.finderbar.jovian.R
import com.finderbar.jovian.adaptor.user.UserAdaptor
import com.finderbar.jovian.databinding.FragmentUsersBinding
import com.finderbar.jovian.viewmodels.user.UsersVM
import com.finderbar.jovian.IFragmentListener
import com.finderbar.jovian.ISearch
import com.finderbar.jovian.activity.UserProfileActivity

/**
 * Created by thein on 12/10/18.
 */
class UsersFragment : Fragment(), ItemUserClick, ISearch {

    private lateinit var adaptor: UserAdaptor
    private lateinit var usersVM: UsersVM
    private lateinit var mIFragmentListener: IFragmentListener

    override fun onItemClick(userId: String, userName: String, avatar: String) {
        val intent = Intent(activity, UserProfileActivity::class.java)
        intent.putExtra("userId", userId)
        intent.putExtra("userName", userName)
        intent.putExtra("avatar", avatar)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        usersVM = ViewModelProviders.of(this).get(UsersVM::class.java)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var binding: FragmentUsersBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_users, container , false)
        var myView : View  = binding.root

        adaptor = UserAdaptor(this)
        binding.userRecyclerView.layoutManager = LinearLayoutManager(activity)
        binding.userRecyclerView.adapter = adaptor

        binding.userSwipeRefreshLayout.setOnRefreshListener {
            usersVM.refresh()
        }

        usersVM.userList.observe(this, Observer { pagedList ->
            adaptor.submitList(pagedList)
        })

        usersVM.loadInitial.observe(this, Observer { loading ->
            val isRefreshing = loading == true
            if (binding.userSwipeRefreshLayout.isRefreshing != isRefreshing) {
                binding.userSwipeRefreshLayout.isRefreshing = isRefreshing
            }
        })

        usersVM.loadBefore.observe(this, Observer {
            adaptor.updateLoadingBefore(it == true)
        })

        usersVM.loadAfter.observe(this, Observer {
            adaptor.updateLoadingAfter(it == true)
        })

        usersVM.networkState.observe(this, Observer {
            adaptor.changeItemWithNetWork(it!!)
        })

        return myView
    }

    override fun onTextQuery(text: String) {
        usersVM.setQuery(text)
        usersVM.refresh()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mIFragmentListener = context as IFragmentListener
        mIFragmentListener!!.addiSearch(this@UsersFragment)
    }

    override fun onDetach() {
        super.onDetach()
        if (null != mIFragmentListener)
            mIFragmentListener!!.removeISearch(this@UsersFragment)
    }
}
