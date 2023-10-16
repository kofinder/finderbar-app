package com.finderbar.jovian.fragments.discuss

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.fetcher.ApolloResponseFetchers
import com.apollographql.apollo.rx2.Rx2Apollo
import com.finderbar.jovian.*
import com.finderbar.jovian.activity.UserProfileActivity
import com.finderbar.jovian.adaptor.CommentAdaptor
import com.finderbar.jovian.databinding.FragmentDiscussCommentBinding
import com.finderbar.jovian.datasource.convertAnswerComment
import com.finderbar.jovian.models.Comment
import com.finderbar.jovian.models.InputCriteria
import com.finderbar.jovian.utilities.AppConstants
import com.finderbar.jovian.utilities.AppConstants.ARG_KEY_DISCUSS_ID
import com.finderbar.jovian.viewmodels.discuss.AskAnswerVM
import es.dmoral.toasty.Toasty
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.DisposableSubscriber
import query.AllCommentByAnswerQuery
import subscription.CommentSubscription


class AnswerCommentFragment: Fragment(), OnCvItemClick {
    companion object {
        const val DRAWABLE_LEFT = 0
        const val DRAWABLE_RIGHT = 2
    }

    private lateinit var askAnswerVm: AskAnswerVM
    private var disposables = CompositeDisposable()
    private var discussId: String? = null
    private lateinit var adaptor: CommentAdaptor
    private lateinit var dialog: ACProgressFlower
    private var queryApi: ApolloCall<AllCommentByAnswerQuery.Data>? = null
    var hasNext = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RxJavaPlugins.setErrorHandler { print(it.message) }
        retainInstance = true
        askAnswerVm = ViewModelProviders.of(this).get(AskAnswerVM::class.java)
        var bundle = savedInstanceState
        if (bundle == null) bundle = arguments
        if (bundle != null) {
            discussId = bundle.getString(ARG_KEY_DISCUSS_ID)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        discussId = arguments?.getString(ARG_KEY_DISCUSS_ID)
        var binding: FragmentDiscussCommentBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_discuss_comment, container , false)
        var myView : View  = binding.root

        dialog = ACProgressFlower.Builder(this.context)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .fadeColor(Color.LTGRAY).build()

        adaptor = CommentAdaptor(this)
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        val dividerItemDecoration = DividerItemDecoration(binding.recyclerView.context, DividerItemDecoration.VERTICAL)
        binding.recyclerView.addItemDecoration(dividerItemDecoration)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = adaptor

        binding.btnSent.setOnClickListener {
            dialog.show()
            askAnswerVm.saveAnswerComment(discussId!!, binding.edInputBody.text.toString())
        }

        askAnswerVm?.result?.observe(this, Observer {
            dialog.dismiss()
            Toasty.success(this.context!!, it!!.message, Toast.LENGTH_SHORT, true).show();
        })

        askAnswerVm?.errorMessage?.observe(this, Observer {
            dialog.dismiss()
            Toasty.error(this.context!!, it!!.message, Toast.LENGTH_SHORT, true).show();
        })

        // get data
        getAllComment(InputCriteria(discussId!!, "", 10, 0), binding.recyclerView)
        getSubScribeComment(discussId!!, binding.recyclerView)

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

    override fun onDestroyView() {
        super.onDestroyView()
        disposables.dispose()
        dialog.dismiss()
    }

    private fun getAllComment(criteria: InputCriteria, recyclerView: RecyclerView) {
        val query = AllCommentByAnswerQuery.builder().criteria(criteria.get()).build()
        queryApi = apolloClient.query(query).responseFetcher(ApolloResponseFetchers.NETWORK_FIRST);
        queryApi?.enqueue({
            adaptor.addAll(convertAnswerComment(it.data()?.allCommentByAnswer()?.comments()))
            adaptor.hasNext = it.data()?.allCommentByAnswer()?.hasNext()!!;
            if(!adaptor.isEmpty()) {
                recyclerView.smoothScrollToPosition(adaptor.itemCount - 1);
            }
        })
    }

    private fun getSubScribeComment(_id: String, recyclerView: RecyclerView) {
        val subApi = apolloClient.subscribe(CommentSubscription.builder()._id(_id).build())
        disposables.add(Rx2Apollo.from(subApi)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSubscriber<Response<CommentSubscription.Data>>() {
                    override fun onNext(res: Response<CommentSubscription.Data>) {
                        val comment = res.data()!!.subscribeComment()
                        adaptor.add(Comment(comment._id(), comment.body(), comment.userId(), comment.userName(), comment.userAvatar(), comment.createdAt()))
                        recyclerView.smoothScrollToPosition(adaptor.itemCount - 1);
                    }
                    override fun onError(t: Throwable) { t.printStackTrace()}
                    override fun onComplete() { print("Subscription exhausted") }
                })
        )
    }

}