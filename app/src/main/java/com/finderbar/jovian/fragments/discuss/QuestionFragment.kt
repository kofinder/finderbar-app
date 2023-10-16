package com.finderbar.jovian.fragments.discuss

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
import com.finderbar.jovian.activity.AskRenderActivity
import com.finderbar.jovian.activity.DiscussActivity
import com.finderbar.jovian.adaptor.discuss.QuestionAdaptor
import com.finderbar.jovian.databinding.FragmentQuestionBinding
import com.finderbar.jovian.models.Question
import com.finderbar.jovian.viewmodels.discuss.QuestionVM
import com.melnykov.fab.FloatingActionButton
import java.io.Serializable


/**
 * Created by finderbar on 12/10/18.
 */
class QuestionFragment: Fragment(), ItemQuestionCallBack, ISearch {

    private lateinit var questionVM: QuestionVM
    private lateinit var adaptor: QuestionAdaptor
    private lateinit var fab: FloatingActionButton
    private lateinit var mIFragmentListener: IFragmentListener

    override fun onItemClick(view: View, item: Question, position: Int) {
        val intent = Intent(activity, DiscussActivity::class.java)
        intent.putExtra("question", item as Serializable?)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        questionVM = ViewModelProviders.of(this).get(QuestionVM::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        var binding: FragmentQuestionBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_question, container , false)
        var myView : View  = binding.root
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)

        adaptor = QuestionAdaptor(this)
        binding.recyclerView.adapter = adaptor
        fab = myView.findViewById(R.id.fab)
        fab.attachToRecyclerView(binding.recyclerView)

        fab.setOnClickListener {
            startActivity(Intent(activity, AskRenderActivity::class.java))
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            questionVM.refresh()
        }

        questionVM.discussList.observe(this, Observer { pagedList ->
            adaptor.submitList(pagedList)
        })

        questionVM.loadingInitial.observe(this, Observer { loading ->
            val isRefreshing = loading == true
            if (binding.swipeRefreshLayout.isRefreshing != isRefreshing) {
                binding.swipeRefreshLayout.isRefreshing = isRefreshing
            }
        })

        questionVM.loadingBefore.observe(this, Observer {
            adaptor.updateLoadingBefore(it == true)
        })

        questionVM.loadingAfter.observe(this, Observer {
            adaptor.updateLoadingAfter(it == true)
        })

        questionVM.networkState.observe(this, Observer {
            adaptor.changeItemWithNetWork(it!!)
        })

        return myView
    }

    override fun onTextQuery(text: String) {
        questionVM.setQuery(text)
        questionVM.refresh()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mIFragmentListener = context as IFragmentListener
        mIFragmentListener!!.addiSearch(this@QuestionFragment)
    }

    override fun onDetach() {
        super.onDetach()
        if (null != mIFragmentListener) {
            mIFragmentListener!!.removeISearch(this@QuestionFragment)
        }
    }

}
