package com.finderbar.jovian.fragments.tutorial

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
import com.finderbar.jovian.*
import com.finderbar.jovian.activity.TutorialActivity
import com.finderbar.jovian.adaptor.tutorial.CategoryAdaptor
import com.finderbar.jovian.databinding.FragmentCategoryBinding
import com.finderbar.jovian.models.Category
import com.finderbar.jovian.viewmodels.tutorial.CategoryVM


/**
 * Created by thein on 12/10/18.
 */
class CategoryFragment : Fragment(), OnEntityItemClick, ISearch  {

    private lateinit var categoryVM: CategoryVM
    private lateinit var adaptor: CategoryAdaptor
    private lateinit var mIFragmentListener: IFragmentListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        categoryVM = ViewModelProviders.of(this).get(CategoryVM::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        var binding: FragmentCategoryBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_category, container , false)
        var myView : View  = binding.root
        adaptor = CategoryAdaptor(this)
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        binding.recyclerView.adapter = adaptor

        binding.swipeRefreshLayout.setOnRefreshListener {
            categoryVM.refresh()
        }

        categoryVM.categories.observe(this, Observer { pagedList ->
            adaptor.submitList(pagedList)
        })

        categoryVM.loadingInitial.observe(this, Observer { loading ->
            val isRefreshing = loading == true
            if (binding.swipeRefreshLayout.isRefreshing != isRefreshing) {
                binding.swipeRefreshLayout.isRefreshing = isRefreshing
            }
        })

        categoryVM.loadingBefore.observe(this, Observer {
            adaptor.updateLoadingBefore(it == true)
        })

        categoryVM.loadingAfter.observe(this, Observer {
            adaptor.updateLoadingAfter(it == true)
        })

        categoryVM.networkState.observe(this, Observer {
            adaptor.changeItemWithNetWork(it!!)
        })

        return myView;
    }

    override fun onItemClick(entity: Category) {
        val intent = Intent(activity, TutorialActivity::class.java)
        intent.putExtra("category", entity)
        startActivity(intent)
    }

    override fun onTextQuery(text: String) {
        categoryVM.setQuery(text)
        categoryVM.refresh()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mIFragmentListener = context as IFragmentListener
        mIFragmentListener!!.addiSearch(this@CategoryFragment)
    }

    override fun onDetach() {
        super.onDetach()
        if (null != mIFragmentListener) {
            mIFragmentListener!!.removeISearch(this@CategoryFragment)
        }
    }
}
