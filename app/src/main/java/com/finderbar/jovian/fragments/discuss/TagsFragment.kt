package com.finderbar.jovian.fragments.discuss

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.finderbar.jovian.*
import com.finderbar.jovian.adaptor.discuss.TagAdaptor
import com.finderbar.jovian.databinding.FragmentTagsBinding
import com.finderbar.jovian.viewmodels.discuss.TagsVM
import java.util.logging.Logger

/**
 * Created by thein on 12/10/18.
 */
class TagsFragment : Fragment(), ISearch, OnCvItemClick {

    companion object {
        var Log = Logger.getLogger(TagsFragment::class.java.name)
    }

    private lateinit var adaptor: TagAdaptor
    private lateinit var tagsVM: TagsVM
    private lateinit var mIFragmentListener: IFragmentListener

    override fun onItemClick(tagId: String) {
        Log.info("tagId ==============>"+ tagId)
//        (activity as MainActivity).viewPager?.currentItem = 0
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tagsVM = ViewModelProviders.of(this).get(TagsVM::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var binding: FragmentTagsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_tags, container , false)
        var myView : View  = binding.root
        adaptor = TagAdaptor(this)
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        val dividerItemDecoration = DividerItemDecoration(binding.recyclerView.context, DividerItemDecoration.VERTICAL)
        binding.recyclerView.addItemDecoration(dividerItemDecoration)
        binding.recyclerView.adapter = adaptor

        binding.swipeRefreshLayout.setOnRefreshListener {
            tagsVM.refresh()
        }

        tagsVM.tagList.observe(this, Observer { pagedList ->
            adaptor.submitList(pagedList)
        })

        tagsVM.loadingInitial.observe(this, Observer { loading ->
            val isRefreshing = loading == true
            if (binding.swipeRefreshLayout.isRefreshing != isRefreshing) {
                binding.swipeRefreshLayout.isRefreshing = isRefreshing
            }
        })

        tagsVM.loadingBefore.observe(this, Observer {
            adaptor.updateLoadingBefore(it == true)
        })

        tagsVM.loadingAfter.observe(this, Observer {
            adaptor.updateLoadingAfter(it == true)
        })

        return myView
    }

    override fun onTextQuery(text: String) {
        tagsVM.setQuery(text)
        tagsVM.refresh()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mIFragmentListener = context as IFragmentListener
        mIFragmentListener!!.addiSearch(this@TagsFragment)
    }

    override fun onDetach() {
        super.onDetach()
        if (null != mIFragmentListener)
            mIFragmentListener!!.removeISearch(this@TagsFragment)
    }
}