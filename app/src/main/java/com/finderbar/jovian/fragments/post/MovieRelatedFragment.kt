package com.finderbar.jovian.fragments.post
import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent.*
import android.databinding.DataBindingUtil
import android.graphics.Point
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.finderbar.jovian.ItemMovieClick
import com.finderbar.jovian.R
import com.finderbar.jovian.activity.MovieDetailActivity
import com.finderbar.jovian.adaptor.post.MovieRelatedAdaptor
import com.finderbar.jovian.databinding.FragmentMovieRelatedBinding
import com.finderbar.jovian.models.Movie
import com.finderbar.jovian.utilities.AppConstants
import com.finderbar.jovian.viewmodels.post.MovieRelatedVM
import android.support.v4.app.ActivityOptionsCompat




class MovieRelatedFragment: Fragment(), ItemMovieClick {

    override fun onItemClick(view: View, position: Int, movie: Movie) {
        val mv = MovieDetailActivity()
        val viewSize = Point(view.width, view.height)
        val videoSize = Point(view.width, view.height)
        val intent = mv.createIntent(view.context, position, movie, viewSize, videoSize, false)
        intent.action = ACTION_VIEW
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity!!)
        startActivityForResult(intent, 100, options.toBundle())
    }

    private lateinit var adaptor: MovieRelatedAdaptor
    private lateinit var movieVM: MovieRelatedVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        movieVM = ViewModelProviders.of(this).get(MovieRelatedVM::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val userId = activity?.intent?.extras?.getString(AppConstants.USER_ID)

        var binding: FragmentMovieRelatedBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_movie_related, container , false)
        var myView : View  = binding.root
        adaptor = MovieRelatedAdaptor(this)

        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        val dividerItemDecoration = DividerItemDecoration(binding.recyclerView.context, DividerItemDecoration.VERTICAL)
        binding.recyclerView.addItemDecoration(dividerItemDecoration)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = adaptor

        binding.swipeRefreshLayout.setOnRefreshListener {
            movieVM.refresh()
        }

        if(userId != null) {
            movieVM.setQuery(userId);
        }

        movieVM.movies.observe(this, Observer { pagedList ->
            adaptor.submitList(pagedList)
        })

        movieVM.loadingInitial.observe(this, Observer { loading ->
            val isRefreshing = loading == true
            if (binding.swipeRefreshLayout.isRefreshing != isRefreshing) {
                binding.swipeRefreshLayout.isRefreshing = isRefreshing
            }
        })

        movieVM.loadingBefore.observe(this, Observer {
            adaptor.updateLoadingBefore(it == true)
        })

        movieVM.loadingAfter.observe(this, Observer {
            adaptor.updateLoadingAfter(it == true)
        })

        return myView;
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
    }


}

