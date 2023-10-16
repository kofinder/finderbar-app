package com.finderbar.jovian.activity

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import com.finderbar.jovian.R
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.finderbar.jovian.viewmodels.user.LoginVM
import es.dmoral.toasty.Toasty
import java.util.*
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.finderbar.jovian.models.MyVideo
import com.finderbar.jovian.prefs
import com.finderbar.jovian.utilities.api.ApiClientUtil
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.SimpleExoPlayerView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import kotlinx.android.synthetic.main.activity_movie_editor_render.*
import mabbas007.tagsedittext.TagsEditText
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class MovieRenderActivity : AppCompatActivity(), TagsEditText.TagsEditListener, View.OnClickListener , EasyPermissions.PermissionCallbacks {

    private var movieRequest = 1
    private var txtTitle: TextView? = null;
    private var txtDescription: TextView? = null;
  //  private var tagStrings: List<String>? = ArrayList()
    private var mediaContainer: FrameLayout? = null;
    private var exoPlayerView:  SimpleExoPlayerView? = null;
    private var coverImage : ImageView? = null;
    private var videoURL = ""

    private var loginVM: LoginVM? = null
    //private var editTagView: EditTag? = null
    private var mTagsEditText: TagsEditText? = null
    private var mToolbar: Toolbar? = null
    private lateinit var mAwesomeValidation: AwesomeValidation
    private lateinit var dialog: ACProgressFlower


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_editor_render)
        mToolbar = findViewById(R.id.toolbar)
        mToolbar!!.title = "Add Movies"
        setSupportActionBar(mToolbar);
        supportActionBar!!.setDisplayHomeAsUpEnabled(true);

        txtTitle = findViewById(R.id.mvTitle);
        txtDescription = findViewById(R.id.mvDescription);
        mediaContainer = findViewById(R.id.mediaContainer);
        mTagsEditText = findViewById(R.id.mvTags)
        coverImage = findViewById(R.id.coverImage)
        exoPlayerView = findViewById(R.id.exoPlayerView);
        exoPlayerView!!.visibility = View.GONE;


        loginVM = ViewModelProviders.of(this).get(LoginVM::class.java);

        mAwesomeValidation = AwesomeValidation(ValidationStyle.BASIC);
        AwesomeValidation.disableAutoFocusOnFirstFailure();
        dialog = ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .fadeColor(Color.LTGRAY).build()


        mediaContainer!!.setOnClickListener(this)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_editor, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.submit -> {
                if (validate()) {
                    dialog.show()
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }


    private fun validate(): Boolean {
        if(txtTitle!!.text.length < 10 || txtTitle!!.text.isNullOrBlank() ) {
            txtTitle!!.error = "Title text must be at least 10 characters";
            return false
        }

        if(txtDescription!!.text.length < 10 || txtDescription!!.text.isNullOrBlank()) {
            txtTitle!!.error = "Description text must be at least 10 characters";
            return false
        }

        if(mTagsEditText!!.tags.isEmpty()) {
            showErrTag!!.error = "Tag must be at least one tag"
            return false
        }

        if(videoURL.isNullOrBlank()) {
            showErrTag!!.error = "please upload your file"
        }

        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onTagsChanged(tags: MutableCollection<String>?) {
        Log.d("fdfdaf", "Tags changed: ")
        Log.d("fdafa", Arrays.toString(tags!!.toTypedArray()))
    }

    override fun onEditingFinished() {
        mTagsEditText!!.clearFocus();
    }

    private fun setUpExoPlayer(videoURL: String) {
        val trackSelector = DefaultTrackSelector(AdaptiveTrackSelection.Factory(DefaultBandwidthMeter()));
        val exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        val videoURI = Uri.parse(videoURL);
        val dataSourceFactory = DefaultHttpDataSourceFactory("ExoFinderPlayer");
        val extractorsFactory = DefaultExtractorsFactory();
        val mediaSource = ExtractorMediaSource(videoURI, dataSourceFactory, extractorsFactory, null, null);
        exoPlayerView!!.player = exoPlayer;
        exoPlayer.prepare(mediaSource);
        exoPlayer.playWhenReady = true;
        coverImage!!.visibility = View.GONE
        exoPlayerView!!.visibility = View.VISIBLE
    }

    override fun onClick(v: View) {
        if(EasyPermissions.hasPermissions(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)){
            val pickIntent = Intent(Intent.ACTION_PICK);
            pickIntent.type = "video/*";
            startActivityForResult(pickIntent, movieRequest);
        } else {
            EasyPermissions.requestPermissions(this,"This application need your permission to access photo gallery.",991, android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == movieRequest && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
//            val filePath = getRealPathFromURIPath(data.data, this);
//            val file = File(filePath);
//            val mFile = RequestBody.create(MediaType?.parse("video/*"), file);
//            val imgName = MultipartBody.Part.createFormData("file", file.name, mFile);
//            uploadToServer(imgName)
//        }

    }

    private fun uploadToServer(imgName: MultipartBody.Part) {
        dialog.show()
        val header = mutableMapOf("x-auth-id" to prefs.userId, "x-auth-token" to prefs.authToken);
        val call = ApiClientUtil.movieUploader().movieUpload(header, imgName);
        call.enqueue(object : Callback<MyVideo> {
            override fun onFailure(call: Call<MyVideo>, t: Throwable) {
                dialog.dismiss()
                Toasty.error(applicationContext, t.message.toString(), Toast.LENGTH_SHORT, true).show();
            }
            override fun onResponse(call: Call<MyVideo>, response: Response<MyVideo>) {
                val body = response.body();
                if(body !== null) {
                    videoURL = body!!.videoUrl
                    setUpExoPlayer(body!!.videoUrl);
                    Toasty.success(applicationContext, "upload completed!", Toast.LENGTH_SHORT, true).show();
                } else {
                    Toasty.error(applicationContext, "No files were uploaded!", Toast.LENGTH_SHORT, true).show();
                }
                dialog.dismiss()
            }
        })
    }

    private fun getRealPathFromURIPath(contentURI: Uri, activity: Activity): String? {
        val cursor = activity.contentResolver.query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.path
        } else {
            cursor.moveToFirst();
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {}

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show();
        }
    }
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            mTagsEditText!!.showDropDown()
        }
    }
}
