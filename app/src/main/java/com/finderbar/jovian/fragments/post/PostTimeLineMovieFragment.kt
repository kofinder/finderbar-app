package com.finderbar.jovian.fragments.post

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.finderbar.jovian.MoviePlayListCallback
import com.finderbar.jovian.MoviePlayerCallback
import com.finderbar.jovian.OnCompleteCallback
import com.finderbar.jovian.R
import com.finderbar.jovian.adaptor.post.PostTimeLineMovieAdaptor
import com.finderbar.jovian.fragments.common.BlackBoardDialogFragment
import com.finderbar.jovian.utilities.ScreenHelper
import com.finderbar.jovian.databinding.FragmentPostTimelineMovieBinding
import com.finderbar.jovian.models.Movie
import com.finderbar.jovian.utilities.AppConstants.ARG_EXTRA_BASE_FB_VIDEO
import com.finderbar.jovian.utilities.AppConstants.ARG_EXTRA_BASE_ORDER
import com.finderbar.jovian.utilities.AppConstants.ARG_EXTRA_PLAYBACK_INFO
import com.finderbar.jovian.utilities.AppConstants.BIG_PLAYER_FRAGMENT_TAG
import com.finderbar.jovian.utilities.AppConstants.BUNDLE_KEY_ORDER
import com.finderbar.jovian.utilities.AppConstants.STATE_KEY_ACTIVE_ORDER
import com.finderbar.jovian.utilities.AppConstants.STATE_KEY_BIG_PLAYER_BUNDLE
import com.finderbar.jovian.utilities.AppConstants.STATE_KEY_FB_VIDEO
import com.finderbar.jovian.utilities.AppConstants.STATE_KEY_PLAYBACK_STATE
import com.finderbar.jovian.utilities.SnapTopLinearSmoothScroller
import com.finderbar.jovian.viewmodels.post.MovieVM
import im.ene.toro.PlayerSelector
import im.ene.toro.ToroPlayer
import im.ene.toro.media.PlaybackInfo
import im.ene.toro.widget.Container
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class PostTimeLineMovieFragment: BlackBoardDialogFragment(), MoviePlayerCallback, OnCompleteCallback {

    private lateinit var movieVM: MovieVM
    private lateinit var adapter: PostTimeLineMovieAdaptor

    private var baseItem: Movie? = null;
    private var baseInfo: PlaybackInfo? = null
    private var baseOrder: Int = 0
    private var callback: MoviePlayListCallback? = null
    private var windowManager: WindowManager? = null
    private var container: Container? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var selector: PlayerSelector? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        movieVM = ViewModelProviders.of(this).get(MovieVM::class.java)
        baseItem = arguments?.getParcelable(ARG_EXTRA_BASE_FB_VIDEO);
        baseInfo = arguments!!.getParcelable(ARG_EXTRA_PLAYBACK_INFO)
        baseOrder = arguments!!.getInt(ARG_EXTRA_BASE_ORDER)
    }

    override fun onCreateView(inflater: LayoutInflater, vg: ViewGroup?, savedInstanceState: Bundle?): View? {
        var binding: FragmentPostTimelineMovieBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_post_timeline_movie, vg , false)
        var myView : View  = binding.root
        container = myView.findViewById(R.id.recycler_view)

        movieVM.movies.observe(this, Observer { pagedList ->
            adapter.submitList(pagedList)
        })

        return myView
    }

    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)

        if (callback != null) {
            callback!!.onPlaylistCreated()
        }

        layoutManager = object : LinearLayoutManager(context) {
            override fun smoothScrollToPosition(view: RecyclerView, state: RecyclerView.State?, position: Int) {
                val linearSmoothScroller = SnapTopLinearSmoothScroller(view.context)
                linearSmoothScroller.targetPosition = position
                super.startSmoothScroll(linearSmoothScroller)
            }
        }

        container!!.layoutManager = layoutManager
        adapter = PostTimeLineMovieAdaptor(baseItem!!, this)
        container!!.adapter = adapter
        container!!.cacheManager = adapter
        container!!.savePlaybackInfo(0, baseInfo!!)
        selector = container!!.playerSelector
        container!!.setHasFixedSize(true);
    }

    override fun onCompleted(player: ToroPlayer) {
        val position = adapter!!.findNextPlayerPosition(player.playerOrder)
        Observable.just(container!!)
                .delay(250, TimeUnit.MILLISECONDS)
                .filter { c -> c != null }
                .subscribe { rv -> rv.smoothScrollToPosition(position) }
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
            container!!.playerSelector = PlayerSelector.NONE
            val video = bundle.getParcelable<Movie>(STATE_KEY_FB_VIDEO)
            val order = bundle.getInt(STATE_KEY_ACTIVE_ORDER)
            if (video != null) {
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
        val firstPlayer = activePlayers[0]  // get the first one only.
        val item = adapter!!.getItem(firstPlayer.playerOrder)

        outState.putInt(STATE_KEY_ACTIVE_ORDER, firstPlayer.playerOrder)
        outState.putParcelable(STATE_KEY_FB_VIDEO, item)
        outState.putParcelable(STATE_KEY_PLAYBACK_STATE, firstPlayer.currentPlaybackInfo)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        if (parentFragment != null && parentFragment is MoviePlayListCallback) {
            this.callback = parentFragment as MoviePlayListCallback
        }
    }

    override fun onDetach() {
        super.onDetach()
        windowManager = null
        callback = null
    }

    override fun onDestroyView() {
        if (callback != null && adapter != null) {
            callback!!.onPlaylistDestroyed(baseOrder, baseItem!!, container!!.getPlaybackInfo(0))
        }
        layoutManager = null
        selector = null
        super.onDestroyView()
    }

    companion object {
        fun newInstance(position: Int, result: Movie, info: PlaybackInfo): PostTimeLineMovieFragment {
            val fragment = PostTimeLineMovieFragment()
            val args = Bundle()
            args.putInt(ARG_EXTRA_BASE_ORDER, position)
            args.putParcelable(ARG_EXTRA_BASE_FB_VIDEO, result)
            args.putParcelable(ARG_EXTRA_PLAYBACK_INFO, info)
            fragment.arguments = args
            return fragment
        }
    }
}