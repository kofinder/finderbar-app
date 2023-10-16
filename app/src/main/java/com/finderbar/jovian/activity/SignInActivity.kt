package com.finderbar.jovian.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.Toast
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.finderbar.jovian.R
import com.finderbar.jovian.viewmodels.user.LoginVM
import es.dmoral.toasty.Toasty
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.finderbar.jovian.fragments.authencation.AuthencationFragment
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.mikepenz.fontawesome_typeface_library.FontAwesome
import com.mikepenz.iconics.IconicsDrawable
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import com.finderbar.jovian.utilities.AppConstants.GOOGLE_LOG_IN_RC
import com.finderbar.jovian.utilities.AppConstants.STATE_INITIALIZED
import com.finderbar.jovian.utilities.AppConstants.PHONE_DIALOG_TAG
import java.util.*


class SignInActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks, View.OnClickListener,
        EasyPermissions.RationaleCallbacks {

    private var loginVM: LoginVM? = null
    private var googleApiClient: GoogleApiClient? = null
    private var callbackManager: CallbackManager? = null


    private var fbButton: Button? = null
    private var gButton: Button? = null
    private var phButton: Button? = null

    private lateinit var googleSignInButton: SignInButton
    private lateinit var facebookSignInButton: LoginButton
    private lateinit var dialog: ACProgressFlower


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginVM = ViewModelProviders.of(this).get(LoginVM::class.java)

        googleSignInButton = findViewById(R.id.google_sign_in_button)
        facebookSignInButton = findViewById(R.id.facebook_sign_in_button)
        fbButton = findViewById(R.id.fb_sign_in_button)
        gButton = findViewById(R.id.g_sign_in_button)
        phButton = findViewById(R.id.phone_sign_in_button)
        fbButton!!.setCompoundDrawablesWithIntrinsicBounds(IconicsDrawable(this).icon(FontAwesome.Icon.faw_facebook).sizeDp(24).color(Color.WHITE), null, null, null)
        gButton!!.setCompoundDrawablesWithIntrinsicBounds(IconicsDrawable(this).icon(FontAwesome.Icon.faw_google).sizeDp(24).color(Color.WHITE), null, null, null)
        phButton!!.setCompoundDrawablesWithIntrinsicBounds(IconicsDrawable(this).icon(FontAwesome.Icon.faw_mobile_alt).sizeDp(24).color(Color.WHITE), null, null, null)

        callbackManager = CallbackManager.Factory.create()
        @Suppress("DEPRECATION")
        facebookSignInButton.setReadPermissions(Arrays.asList("email", "public_profile"))

        // setup facebook
        facebookSignInButton.registerCallback(callbackManager, object: FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                handleFacebookAccessToken(result.accessToken)
            }
            override fun onCancel() {
                Toasty.warning(applicationContext, "Cancel!", Toast.LENGTH_SHORT, true).show()
            }
            override fun onError(error: FacebookException?) {
                Toasty.error(applicationContext, error?.message!!, Toast.LENGTH_SHORT, true).show()
            }
        })

        // setup google
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build()
        googleApiClient = GoogleApiClient.Builder(this@SignInActivity)
                .enableAutoManage(this@SignInActivity) { }
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build()

        gButton?.setOnClickListener(this)
        phButton?.setOnClickListener(this)

        dialog = ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .fadeColor(Color.LTGRAY).build()

        loginVM?.getUser()?.observe(this, Observer {
            if (it != null && it.isLogin()) {
               mainScreen()
            }
        })

        loginVM?.errorMessage?.observe(this, Observer {
            dialog.dismiss()
            Toasty.error(this, it!!.message, Toast.LENGTH_SHORT, true).show()
        })

        FirebaseAuth.getInstance().currentUser?.let { loginVM?.login(it) }
    }

    private fun showPhDialog(state: Int) {
        val fm = supportFragmentManager.beginTransaction()
        val fragDialog = AuthencationFragment()
        val bundle = Bundle()
        bundle.putInt("uiState", state);
        fragDialog.arguments = bundle;
        fragDialog.show(fm, PHONE_DIALOG_TAG)
    }


    override fun onClick(v: View?) {
        when (v) {
            fbButton -> facebookSignInButton.performClick()
            phButton -> showPhDialog(STATE_INITIALIZED)
            gButton -> startActivityForResult(Auth.GoogleSignInApi.getSignInIntent(googleApiClient), GOOGLE_LOG_IN_RC)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data!!)
        if(requestCode== GOOGLE_LOG_IN_RC){
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess) {
                 handleGoogleAccessToken(result.signInAccount!!)
             } else {
                 Toasty.error(this, "Some error occurred.", Toast.LENGTH_SHORT).show()
             }
        } else {
             callbackManager?.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun handleFacebookAccessToken(accessToken: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(accessToken.token)
        FirebaseAuth.getInstance().signInWithCredential(credential)?.addOnCompleteListener(this, OnCompleteListener<AuthResult> {
            if(!it.isSuccessful){
                dialog.dismiss()
                Toasty.error(applicationContext, "facebook error login", Toast.LENGTH_LONG).show()
            } else {
                val auth = FirebaseAuth.getInstance().currentUser
                dialog.show()
                loginVM?.login(auth!!)
            }
        })
    }

    private fun handleGoogleAccessToken(acc: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acc.idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)?.addOnCompleteListener(this, OnCompleteListener<AuthResult> {
            if(!it.isSuccessful){
                dialog.dismiss()
                Toasty.error(applicationContext, "google error login", Toast.LENGTH_LONG).show()
            } else {
                val auth = FirebaseAuth.getInstance().currentUser
                dialog.show()
                loginVM?.login(auth!!)
            }
        })
    }

    override fun onRationaleDenied(requestCode: Int) {
        Toasty.warning(this, "onRationaleDenied", Toast.LENGTH_LONG).show()
    }

    override fun onRationaleAccepted(requestCode: Int) {
        Toasty.warning(this, "onRationaleAccepted", Toast.LENGTH_LONG).show()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        Toasty.warning(this, "onPermissionsDenied", Toast.LENGTH_LONG).show()
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Toasty.warning(this, "onPermissionsGranted", Toast.LENGTH_LONG).show()
    }

    private fun mainScreen() {
        dialog.dismiss()
        //Toasty.success(applicationContext, "successfully login", Toast.LENGTH_LONG).show()
        val intent = Intent(this@SignInActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

}
