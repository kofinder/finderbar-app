package com.finderbar.jovian.activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.finderbar.jovian.R
import com.finderbar.jovian.adaptor.ViewPagerAdapter

import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.net.Uri
import android.view.View

import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import com.finderbar.jovian.App
import com.finderbar.jovian.ItemMovieClick
import com.finderbar.jovian.fragments.job.JobFragment
import com.finderbar.jovian.fragments.post.MovieDescriptionFragment
import com.finderbar.jovian.fragments.post.MovieRelatedFragment
import com.finderbar.jovian.models.Movie
import com.finderbar.jovian.utilities.AppConstants.EXTRA_MEDIA_ORDER
import com.finderbar.jovian.utilities.AppConstants.EXTRA_MEDIA_URI
import com.finderbar.jovian.utilities.AppConstants.EXTRA_MEDIA_PLAYBACK_INFO
import com.finderbar.jovian.utilities.AppConstants.EXTRA_MEDIA_DESCRIPTION
import com.finderbar.jovian.utilities.AppConstants.EXTRA_MEDIA_PLAYER_SIZE
import com.finderbar.jovian.utilities.AppConstants.EXTRA_MEDIA_VIDEO_SIZE
import com.finderbar.jovian.utilities.AppConstants.EXTRA_DEFAULT_FULLSCREEN
import com.finderbar.jovian.utilities.AppConstants.EXTRA_MEDIA_IMAGE
import com.finderbar.jovian.utilities.AppConstants.USER_ID
import com.finderbar.jovian.utilities.AppConstants.MOVIE_ID
import com.finderbar.jovian.viewmodels.post.MovieDetailVM

import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource

import com.google.android.exoplayer2.ui.PlayerView
import im.ene.toro.exoplayer.Playable
import im.ene.toro.exoplayer.ToroExo
import im.ene.toro.media.PlaybackInfo
import im.ene.toro.exoplayer.ExoCreator


class MovieDetailActivity: AppCompatActivity(), ItemMovieClick {

    override fun onItemClick(view: View, position: Int, movie: Movie) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(movie.videoUrl)))
    }

    private lateinit var movieVM: MovieDetailVM
    private lateinit var mToolbar: Toolbar
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager
    private var order: Int = 0
    private var mediaUri: Uri? = null
    private var playbackInfo: PlaybackInfo? = null
    private var content: String? = null
    private var playerSize: Point? = null
    private var videoSize: Point? = null
    private var fullScreen: Boolean? = null
    private var playerView: PlayerView? = null
    private var coverImage: ImageView? = null
    private var creator: ExoCreator? = null
    private var mediaSource: MediaSource? =null
    private var exoPlayer: SimpleExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        movieVM = ViewModelProviders.of(this).get(MovieDetailVM::class.java)

        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_movie_detail);

        val extras = intent.extras
        if (extras != null) {
            order = extras.getInt(EXTRA_MEDIA_ORDER)
            mediaUri = extras.getParcelable(EXTRA_MEDIA_URI)
            playbackInfo = extras.getParcelable(EXTRA_MEDIA_PLAYBACK_INFO)
            content = extras.getString(EXTRA_MEDIA_DESCRIPTION)
            playerSize = extras.getParcelable(EXTRA_MEDIA_PLAYER_SIZE)
            videoSize = extras.getParcelable(EXTRA_MEDIA_VIDEO_SIZE)
            fullScreen = extras.getBoolean(EXTRA_DEFAULT_FULLSCREEN, false)
        }

        mToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(mToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        playerView = findViewById(R.id.player_view)
        creator = App.exoCreator
        exoPlayer = ToroExo.with(this).requestPlayer(creator!!)
        mediaSource = creator!!.createMediaSource(mediaUri!!, null)
        exoPlayer!!.addListener(listener)
        exoPlayer!!.prepare(mediaSource)
        playerView!!.player = exoPlayer

        coverImage = findViewById(R.id.cover_image)
        viewPager = findViewById(R.id.viewpager)
        tabLayout = findViewById(R.id.sliding_tabs)

        setupViewPager(viewPager)
        tabLayout.setupWithViewPager(viewPager)
    }

    private fun setupViewPager(viewPager: ViewPager) {
        var adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFrag(MovieDescriptionFragment(), "Description")
        adapter.addFrag(JobFragment(), "Comments")
        adapter.addFrag(MovieRelatedFragment(), "Next")
        viewPager.adapter = adapter
    }

   fun createIntent(base: Context, order: Int, movie: Movie,
                     playerSize: Point, videoSize: Point, fullScreen: Boolean): Intent {
        val intent = Intent(base, MovieDetailActivity::class.java)
        val extras = Bundle()
        extras.putString(MOVIE_ID, movie.id)
        extras.putString(USER_ID, movie.userId)
//        extras.putString(MOVIE_TITLE, movie.title)
//        extras.putString(EXTRA_MEDIA_DESCRIPTION, movie.description)
        extras.putString(EXTRA_MEDIA_IMAGE, movie.coverUrl)
        extras.putParcelable(EXTRA_MEDIA_URI, Uri.parse(movie.videoUrl))

        extras.putParcelable(EXTRA_MEDIA_PLAYBACK_INFO, playbackInfo)
        extras.putParcelable(EXTRA_MEDIA_PLAYER_SIZE, playerSize)
        extras.putParcelable(EXTRA_MEDIA_VIDEO_SIZE, videoSize)
        extras.putBoolean(EXTRA_DEFAULT_FULLSCREEN, fullScreen)
        intent.putExtras(extras)

        return intent
    }

    private val listener = (object: Playable.DefaultEventListener() {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            super.onPlayerStateChanged(playWhenReady, playbackState)
            val active = playbackState > Player.STATE_IDLE && playbackState < Player.STATE_ENDED
            playerView!!.keepScreenOn = active
        }
    })

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onStart() {
        super.onStart()
        exoPlayer!!.playWhenReady = true
        coverImage!!.visibility = View.INVISIBLE
    }

    override fun onStop() {
        super.onStop()
        exoPlayer!!.playWhenReady = false
        coverImage!!.visibility = View.GONE
    }

    override fun  onDestroy() {
        super.onDestroy()
        playerView!!.player = null
        exoPlayer!!.removeListener(listener)
        ToroExo.with(this).releasePlayer(creator!!, exoPlayer!!)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val fragment = supportFragmentManager.findFragmentByTag("MovieRelatedFragment")
        fragment?.onActivityResult(requestCode, resultCode, data)
    }
}