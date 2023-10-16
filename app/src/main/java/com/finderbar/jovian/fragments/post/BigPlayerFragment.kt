package com.finderbar.jovian.fragments.post

import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.finderbar.jovian.MoviePlayerCallback
import com.finderbar.jovian.R
import com.finderbar.jovian.fragments.common.BlackBoardDialogFragment
import com.finderbar.jovian.utilities.ScreenHelper
import com.finderbar.jovian.databinding.FragmentDialogBigplayerBinding
import com.finderbar.jovian.models.Movie
import com.finderbar.jovian.utilities.AppConstants.ARG_KEY_INIT_INFO
import com.finderbar.jovian.utilities.AppConstants.ARG_KEY_VIDEO_ITEM
import com.finderbar.jovian.utilities.AppConstants.ARG_KEY_VIDEO_ORDER
import com.finderbar.jovian.utilities.AppConstants.BUNDLE_KEY_INFO
import com.finderbar.jovian.utilities.AppConstants.BUNDLE_KEY_ORDER
import com.finderbar.jovian.utilities.AppConstants.BUNDLE_KEY_VIDEO
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import im.ene.toro.exoplayer.Playable
import im.ene.toro.exoplayer.ToroExo
import im.ene.toro.media.PlaybackInfo

class BigPlayerFragment: BlackBoardDialogFragment()  {

    private var windowManager: WindowManager? = null
    private var callback: MoviePlayerCallback? = null
    private var videoOrder: Int = 0
    private var videoItem: Movie? = null
    private var playbackInfo: PlaybackInfo? = null
    private var playerHelper: Playable? = null
    lateinit var playerView: PlayerView

    val currentState: Bundle
        get() {
            var info = playbackInfo
            if (playerHelper != null) info = playerHelper!!.playbackInfo
            val bundle = Bundle()
            bundle.putParcelable(BUNDLE_KEY_VIDEO, videoItem)
            bundle.putInt(BUNDLE_KEY_ORDER, videoOrder)
            bundle.putParcelable(BUNDLE_KEY_INFO, info)
            return bundle
        }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (parentFragment != null && parentFragment is MoviePlayerCallback) {
            this.callback = parentFragment as MoviePlayerCallback?
        }
    }

    override fun onDetach() {
        super.onDetach()
        callback = null
        windowManager = null
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var bundle = savedInstanceState
        if (bundle == null) bundle = arguments
        if (bundle != null) {
            videoItem = bundle.getParcelable(ARG_KEY_VIDEO_ITEM)
            playbackInfo = bundle.getParcelable(ARG_KEY_INIT_INFO)
            videoOrder = bundle.getInt(ARG_KEY_VIDEO_ORDER)
        }
        if (videoItem == null) throw IllegalArgumentException("Require a Video item.")
        if (playbackInfo == null) playbackInfo = PlaybackInfo()
        windowManager = if (context == null)
            null
        else
            context!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        if (windowManager == null || !ScreenHelper.shouldUseBigPlayer( windowManager!!.defaultDisplay)) {
            dismissAllowingStateLoss()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var binding: FragmentDialogBigplayerBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_dialog_bigplayer, container , false)
        var myView : View  = binding.root
        playerView = myView.findViewById(R.id.big_player)
        return myView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (callback != null) {
            callback!!.onBigPlayerCreated()
        }
        playerView.keepScreenOn = true
        val windowSize = Point()
        windowManager!!.defaultDisplay.getSize(windowSize)
        if (windowSize.y * MOVIE_RATIO >= windowSize.x) {
            playerView!!.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH)
        } else {
            playerView!!.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val rotation = windowManager!!.defaultDisplay.rotation
        if (rotation % 2 != 0) {
            val window = dialog!!.window
            if (window != null) {
                val decorView = window.decorView
                val immersiveStickyFrag: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                } else {
                    4096
                }
                val uiOptions = (View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or immersiveStickyFrag)
                decorView.systemUiVisibility = uiOptions
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (playerHelper == null) {
            playerHelper = ToroExo.with(context).defaultCreator.createPlayable(Uri.parse(videoItem!!.videoUrl), null)
            playerHelper!!.prepare(true)
        }
        playerHelper!!.playerView = playerView
        playerHelper!!.playbackInfo = playbackInfo!!
        playerHelper!!.play()
    }

    override fun onStop() {
        super.onStop()
        if (playerHelper != null) playerHelper!!.pause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(ARG_KEY_VIDEO_ORDER, videoOrder)
        outState.putParcelable(ARG_KEY_VIDEO_ITEM, videoItem)
        if (playerHelper != null) playbackInfo = playerHelper!!.playbackInfo
        outState.putParcelable(ARG_KEY_INIT_INFO, playbackInfo)
    }

    override fun onDestroyView() {
        if (playerHelper != null) {
            playbackInfo = playerHelper!!.playbackInfo
            playerHelper!!.playerView = null
            playerHelper!!.release()
            playerHelper = null
        }
        if (callback != null) {
            callback!!.onBigPlayerDestroyed(videoOrder, videoItem, playbackInfo)
        }
        super.onDestroyView()
    }

    companion object {
        const val MOVIE_RATIO: Float = 2.4f
        fun newInstance(order: Int, video: Movie, info: PlaybackInfo?): BigPlayerFragment {
            val fragment = BigPlayerFragment()
            val args = Bundle()
            args.putInt(ARG_KEY_VIDEO_ORDER, order)
            args.putParcelable(ARG_KEY_VIDEO_ITEM, video)
            if (info != null) args.putParcelable(ARG_KEY_INIT_INFO, info)
            fragment.arguments = args
            return fragment
        }
    }
}