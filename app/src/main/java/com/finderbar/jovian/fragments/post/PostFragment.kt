package com.finderbar.jovian.fragments.post

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.finderbar.jovian.*
import com.finderbar.jovian.activity.MovieRenderActivity
import com.finderbar.jovian.adaptor.post.PostAdaptor
import com.finderbar.jovian.viewholder.MovieViewHolder
import com.finderbar.jovian.databinding.FragmentPostBinding
import com.finderbar.jovian.models.Movie
import com.finderbar.jovian.viewmodels.post.PostVM
import com.melnykov.fab.FloatingActionButton
import im.ene.toro.PlayerSelector
import im.ene.toro.media.PlaybackInfo
import im.ene.toro.widget.Container

class PostFragment : Fragment(), ItemMovieCallback, ISearch, MoviePlayListCallback, MoviePlayerCallback {

    private lateinit var adaptor: PostAdaptor
    private lateinit var container: Container
    private lateinit var fab: FloatingActionButton
    private lateinit var postVM: PostVM
    private var selector = PlayerSelector.DEFAULT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postVM = ViewModelProviders.of(this).get(PostVM::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, viewGroup: ViewGroup?, savedInstanceState: Bundle?): View? {
        var binding: FragmentPostBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_post, viewGroup , false)
        var myView : View  = binding.root
        container = binding.playerContainer

        adaptor = PostAdaptor(this)
        binding.playerContainer.layoutManager = LinearLayoutManager(activity)
        binding.playerContainer.adapter = adaptor
        container!!.playerSelector = selector

        binding.swipeRefreshLayout.setOnRefreshListener {
            postVM.refresh()
        }

        postVM.posts.observe(this, Observer { pagedList ->
            adaptor.submitList(pagedList)
        })

        postVM.loadingInitial.observe(this, Observer { loading ->
            val isRefreshing = loading == true
            if (binding.swipeRefreshLayout.isRefreshing != isRefreshing) {
                binding.swipeRefreshLayout.isRefreshing = isRefreshing
            }
        })

        postVM.loadingBefore.observe(this, Observer {
            adaptor.updateLoadingBefore(it == true)
        })

        postVM.loadingAfter.observe(this, Observer {
            adaptor.updateLoadingAfter(it == true)
        })

        postVM.networkState.observe(this, Observer {
            adaptor.changeItemWithNetWork(it!!)
        })

        fab = myView.findViewById(R.id.btn_fab)
        fab.attachToRecyclerView(binding.playerContainer)
        fab.setOnClickListener {
            startActivity(Intent(activity, MovieRenderActivity::class.java))
        }
        return myView;
    }

    override fun onPlaylistCreated() {
        container!!.playerSelector = PlayerSelector.NONE
    }

    override fun onPlaylistDestroyed(basePosition: Int, result: Movie, latestInfo: PlaybackInfo) {
        container!!.savePlaybackInfo(basePosition, latestInfo)
        container!!.playerSelector = selector
    }

    override fun onBigPlayerCreated() {
        container!!.playerSelector = PlayerSelector.NONE
    }

    override fun onBigPlayerDestroyed(order: Int, baseItem: Movie?, latestInfo: PlaybackInfo?) {
        if (latestInfo != null) {
            container!!.savePlaybackInfo(order, latestInfo)
        }
        container!!.playerSelector = selector
    }

    override fun onTextQuery(text: String) {
        postVM.setQuery(text)
        postVM.refresh()
    }


    override fun onItemClick(viewHolder: MovieViewHolder, view: View, item: Movie, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}