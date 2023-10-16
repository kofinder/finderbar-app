package com.finderbar.jovian.fragments.job

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
import com.finderbar.jovian.IFragmentListener
import com.finderbar.jovian.ISearch
import com.finderbar.jovian.OnCvItemClick
import com.finderbar.jovian.R
import com.finderbar.jovian.activity.AskRenderActivity
import com.finderbar.jovian.activity.JobDetailActivity
import com.finderbar.jovian.adaptor.job.JobAdaptor
import com.finderbar.jovian.databinding.FragmentJobsBinding
import com.finderbar.jovian.viewmodels.job.JobVM
import com.melnykov.fab.FloatingActionButton

/**
 * Created by thein on 12/10/18.
 */
class JobFragment : Fragment(), OnCvItemClick, ISearch {

    private lateinit var jobVM: JobVM
    private lateinit var adaptor: JobAdaptor
    private lateinit var fab: FloatingActionButton
    private lateinit var mIFragmentListener: IFragmentListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        jobVM = ViewModelProviders.of(this).get(JobVM::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var binding: FragmentJobsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_jobs, container , false)
        var myView : View  = binding.root
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)

        adaptor = JobAdaptor(this)
        binding.recyclerView.adapter = adaptor
        fab = myView.findViewById(R.id.fab)
        fab.attachToRecyclerView(binding.recyclerView)
        fab.setOnClickListener {
            startActivity(Intent(activity, AskRenderActivity::class.java))
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            jobVM.refresh()
        }

        jobVM.jobs.observe(this, Observer { pagedList ->
            adaptor.submitList(pagedList)
        })

        jobVM.loadingInitial.observe(this, Observer { loading ->
            val isRefreshing = loading == true
            if (binding.swipeRefreshLayout.isRefreshing != isRefreshing) {
                binding.swipeRefreshLayout.isRefreshing = isRefreshing
            }
        })

        jobVM.loadingBefore.observe(this, Observer {
            adaptor.updateLoadingBefore(it == true)
        })

        jobVM.loadingAfter.observe(this, Observer {
            adaptor.updateLoadingAfter(it == true)
        })

        jobVM.networkState.observe(this, Observer {
            adaptor.changeItemWithNetWork(it!!)
        })

        return myView
    }

    override fun onItemClick(disussId: String) {
        val intent = Intent(activity, JobDetailActivity::class.java)
        intent.putExtra("discussId", disussId)
        startActivity(intent)
    }

    override fun onTextQuery(text: String) {
        jobVM.setQuery(text)
        jobVM.refresh()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mIFragmentListener = context as IFragmentListener
        mIFragmentListener!!.addiSearch(this@JobFragment)
    }

    override fun onDetach() {
        super.onDetach()
        if (null != mIFragmentListener) {
            mIFragmentListener!!.removeISearch(this@JobFragment)
        }
    }
}