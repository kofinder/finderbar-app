package com.finderbar.jovian.fragments.post

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.finderbar.jovian.*
import com.finderbar.jovian.databinding.FragmentMovieBinding
import com.finderbar.jovian.activity.MovieRenderActivity
import com.finderbar.jovian.adaptor.post.MovieAdaptor
import com.finderbar.jovian.viewholder.MovieViewHolder
import com.finderbar.jovian.utilities.ScreenHelper
import com.finderbar.jovian.models.Movie
import com.finderbar.jovian.utilities.AppConstants.BIG_PLAYER_FRAGMENT_TAG
import com.finderbar.jovian.utilities.AppConstants.BUNDLE_KEY_ORDER
import com.finderbar.jovian.utilities.AppConstants.STATE_KEY_ACTIVE_ORDER
import com.finderbar.jovian.utilities.AppConstants.STATE_KEY_BIG_PLAYER_BUNDLE
import com.finderbar.jovian.utilities.AppConstants.STATE_KEY_FB_VIDEO
import com.finderbar.jovian.utilities.AppConstants.STATE_KEY_PLAYBACK_STATE
import com.finderbar.jovian.viewmodels.post.MovieVM
import com.melnykov.fab.FloatingActionButton
import im.ene.toro.PlayerSelector
import im.ene.toro.ToroPlayer
import im.ene.toro.media.PlaybackInfo
import im.ene.toro.widget.Container

class MovieFragment : Fragment(), ItemMovieCallback, ISearch, MoviePlayListCallback, MoviePlayerCallback {

    private lateinit var adaptor: MovieAdaptor
    private lateinit var movieVM: MovieVM
    private lateinit var fab: FloatingActionButton
    private lateinit var mIFragmentListener: IFragmentListener

    private lateinit var container: Container
    private var windowManager: WindowManager? = null
    private var selector = PlayerSelector.DEFAULT
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        movieVM = ViewModelProviders.of(this).get(MovieVM::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, vg: ViewGroup?, savedInstanceState: Bundle?): View? {
        var binding: FragmentMovieBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_movie, vg , false)
        var myView : View  = binding.root

        container = binding.playerContainer

        adaptor = MovieAdaptor(this)
        binding.playerContainer.layoutManager = LinearLayoutManager(activity)
        binding.playerContainer.adapter = adaptor
        container!!.playerSelector = selector

        binding.swipeRefreshLayout.setOnRefreshListener {
            movieVM.refresh()
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

        movieVM.networkState.observe(this, Observer {
            adaptor.changeItemWithNetWork(it!!)
        })

        fab = myView.findViewById(R.id.btn_fab)
        fab.attachToRecyclerView(binding.playerContainer)
        fab.setOnClickListener {
            startActivity(Intent(activity, MovieRenderActivity::class.java))
        }

        return myView;
    }

    override fun onViewStateRestored(bundle: Bundle?) {
        super.onViewStateRestored(bundle)
        if (bundle == null) {
            return
        }
        val playerBundle = bundle.getBundle(STATE_KEY_BIG_PLAYER_BUNDLE)
        if (playerBundle != null) {
            val order = playerBundle.getInt(BUNDLE_KEY_ORDER)
            var info: PlaybackInfo? = null;
            if (info == null) info = PlaybackInfo()
            this.container!!.savePlaybackInfo(order, info)
        }

        if (ScreenHelper.shouldUseBigPlayer(windowManager!!.defaultDisplay)) {
            val video = bundle.getParcelable<Movie>(STATE_KEY_FB_VIDEO) // can be null.
            if (video != null) {
                container!!.playerSelector = PlayerSelector.NONE
                val order = bundle.getInt(STATE_KEY_ACTIVE_ORDER)
                val info = bundle.getParcelable<PlaybackInfo>(STATE_KEY_PLAYBACK_STATE)
                val playerFragment = BigPlayerFragment.newInstance(order, video, info)
                playerFragment.show(childFragmentManager, BIG_PLAYER_FRAGMENT_TAG)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val playerFragment = childFragmentManager.findFragmentByTag(BIG_PLAYER_FRAGMENT_TAG)
        if (playerFragment is BigPlayerFragment) {
            val playerBundle = playerFragment.currentState
            outState.putBundle(STATE_KEY_BIG_PLAYER_BUNDLE, playerBundle)
        }

        val activePlayers = container!!.filterBy(Container.Filter.PLAYING)
        if (activePlayers.isEmpty()) return
        val firstPlayer = activePlayers[0]
        val item = adaptor.getItem(firstPlayer.playerOrder)
        if (item is Movie) {
            outState.putInt(STATE_KEY_ACTIVE_ORDER, firstPlayer.playerOrder)
            outState.putParcelable(STATE_KEY_FB_VIDEO, item)
            outState.putParcelable(STATE_KEY_PLAYBACK_STATE, firstPlayer.currentPlaybackInfo)
        }
    }

    override fun onItemClick(viewHolder: MovieViewHolder, view: View, item: Movie, position: Int) {
        if (viewHolder is ToroPlayer && item is Movie) {
            val info = (viewHolder as ToroPlayer).currentPlaybackInfo
            val moreVideos = PostTimeLineMovieFragment.newInstance(position, item, info)
            moreVideos.show(childFragmentManager, "TimeLineMovies")
        }
    }

    override fun onTextQuery(text: String) {
        movieVM.setQuery(text)
        movieVM.refresh()
    }

    override fun onDestroyView() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroyView()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        mIFragmentListener = context as IFragmentListener
        mIFragmentListener!!.addiSearch(this@MovieFragment)
    }

    override fun onDetach() {
        super.onDetach()
        if (null != mIFragmentListener) {
            mIFragmentListener!!.removeISearch(this@MovieFragment)
        }
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

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        selector = if (isVisibleToUser) {
            PlayerSelector.DEFAULT
        } else {
            PlayerSelector.NONE
        }
        handler.postDelayed({ if (container != null) container!!.playerSelector = selector }, 200)
    }
}




//    override fun onItemClick(view: View, position: Int, movie: Movie, playbackInfo: PlaybackInfo) {
//        val viewSize = Point(view.width, view.height)
//        val videoSize = Point(view.width, view.height)
//        val intent = Intent(view.context, MovieDetailActivity::class.java)
//        intent.putExtra(AppConstants.MOVIE_ID, movie.id)
//        intent.putExtra(AppConstants.USER_ID, movie.userId)
//        intent.putExtra(AppConstants.MOVIE_TITLE, movie.title)
//        intent.putExtra(AppConstants.EXTRA_MEDIA_URI, Uri.parse(movie.videoUrl))
//        intent.putExtra(AppConstants.EXTRA_MEDIA_ORDER, position)
//        intent.putExtra(AppConstants.EXTRA_MEDIA_DESCRIPTION, movie.description)
//        intent.putExtra(AppConstants.EXTRA_MEDIA_PLAYBACK_INFO, playbackInfo)
//        intent.putExtra(AppConstants.EXTRA_MEDIA_PLAYER_SIZE, viewSize)
//        intent.putExtra(AppConstants.EXTRA_MEDIA_VIDEO_SIZE, videoSize)
//        intent.putExtra(AppConstants.EXTRA_DEFAULT_FULLSCREEN, false)
//        intent.putExtra(AppConstants.STATE_MEDIA_PLAYBACK_INFO, movie.id)
//        intent.putExtra(AppConstants.RESULT_EXTRA_PLAYER_ORDER, movie.id)
//        intent.putExtra(AppConstants.RESULT_EXTRA_PLAYBACK_INFO, movie.id)
//        startActivity(intent)
//    }
