package com.finderbar.jovian.fragments.authencation

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.TextUtils
import com.basgeekball.awesomevalidation.ValidationStyle.BASIC
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.finderbar.jovian.R
import com.finderbar.jovian.activity.MainActivity
import com.finderbar.jovian.utilities.phonefield.PhoneInputLayout
import com.finderbar.jovian.prefs
import com.finderbar.jovian.viewmodels.user.LoginVM

import com.finderbar.jovian.utilities.AppConstants.STATE_INITIALIZED
import com.finderbar.jovian.utilities.AppConstants.STATE_VERIFY_FAILED
import com.finderbar.jovian.utilities.AppConstants.STATE_SIGN_IN_FAILED

import com.finderbar.jovian.utilities.AppConstants.STATE_VERIFY_SUCCESS
import com.finderbar.jovian.utilities.AppConstants.STATE_SIGN_IN_SUCCESS
import com.finderbar.jovian.utilities.AppConstants.STATE_CODE_SENT
import com.finderbar.jovian.utilities.AppConstants.KEY_VERIFY_IN_PROGRESS


import es.dmoral.toasty.Toasty
import pub.devrel.easypermissions.AppSettingsDialog
import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit


class AuthencationFragment : DialogFragment(), View.OnClickListener {

    private lateinit var mAwesomeValidation: AwesomeValidation
    private lateinit var dialog: ACProgressFlower

    private lateinit var phInputLayout: PhoneInputLayout
    private var txtVerificationCode: EditText? = null
    private var loginVM: LoginVM? = null

    private var btnVerification: Button? = null
    private var btnVerificationCode: Button? = null
    private var btnResend: Button? = null

    private var layoutVerification: LinearLayout? = null
    private var layoutVerificationCode: LinearLayout? = null
    private var txtFullName: EditText? = null

    private var auth: FirebaseAuth? = null
    private var verificationInProgress = false
    private var storedVerificationId: String? = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle)
        loginVM = ViewModelProviders.of(this).get(LoginVM::class.java)
        if (savedInstanceState != null) {
            verificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS)
            updateUI(auth?.currentUser)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_dialog_auth_login, container, false)
        phInputLayout = rootView.findViewById(R.id.phone_input_layout)
        txtVerificationCode = rootView.findViewById(R.id.txt_verification_code)
        txtFullName = rootView.findViewById(R.id.txt_full_name)

        btnVerification = rootView.findViewById(R.id.btn_verification)
        btnVerificationCode = rootView.findViewById(R.id.btn_verification_code)
        btnResend = rootView.findViewById(R.id.btn_resend)

        layoutVerification = rootView.findViewById(R.id.layout_verification)
        layoutVerificationCode = rootView.findViewById(R.id.layout_verification_code)


        btnVerification?.setOnClickListener(this)
        btnVerificationCode?.setOnClickListener(this)
        btnResend?.setOnClickListener(this)

        auth = FirebaseAuth.getInstance()

        mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationFailed(ex: FirebaseException?) {
                verificationInProgress = false
                if (ex is FirebaseAuthInvalidCredentialsException) {
                    phInputLayout.editText.error = getString(R.string.err_tel)
                } else if (ex is FirebaseTooManyRequestsException) {
                    Toasty.error(activity!!.applicationContext, ex.message!!, Toast.LENGTH_SHORT, true).show()
                }
                updateUI(STATE_VERIFY_FAILED)
            }

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                verificationInProgress = false
                updateUI(STATE_VERIFY_SUCCESS, credential)
                signInWithPhoneAuthCredential(credential)
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken ) {
                storedVerificationId = verificationId
                resendToken = token
                updateUI(STATE_CODE_SENT)
            }
        }

        dialog = ACProgressFlower.Builder(this.context)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .fadeColor(Color.LTGRAY).build()
        mAwesomeValidation = AwesomeValidation(BASIC)
        AwesomeValidation.disableAutoFocusOnFirstFailure()

        loginVM?.getUser()?.observe(this, Observer {
            if (it != null && it.isLogin()) {
                mainScreen()
            }
        })

        loginVM?.errorMessage?.observe(this, Observer {
            dialog.dismiss()
            Toasty.error(activity!!.applicationContext, it!!.message, Toast.LENGTH_SHORT, true).show()
        })

        FirebaseAuth.getInstance().currentUser?.let { loginVM?.login(it) }
        updateUI(STATE_INITIALIZED)


        return rootView
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, verificationInProgress)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_verification -> {
                if( txtFullName?.text?.length!! < 5 || txtFullName?.text.isNullOrBlank()) {
                    txtFullName?.error = "Enter Your Name?"
                    return
                }
                prefs.fullName = txtFullName?.text.toString()

                if (!phInputLayout.isValid) {
                    phInputLayout.editText.error = getString(R.string.err_tel)
                    return
                }
                dialog.show()
                startPhoneNumberVerification(phInputLayout.phoneNumber)
            }
            R.id.btn_verification_code -> {
                dialog.show()
                val code = txtVerificationCode?.text.toString()
                if(TextUtils.isEmpty(code)) {
                    txtVerificationCode?.error = "Cannot be empty."
                    return
                }
                verifyPhoneNumberWithCode(storedVerificationId, code)
            }
            R.id.btn_resend -> {
                dialog.show()
                resendVerificationCode(phInputLayout.phoneNumber, resendToken)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        updateUI(auth?.currentUser)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data!!)
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            Toasty.warning(activity!!, getString(R.string.returned_from_app_settings_to_activity), Toast.LENGTH_LONG).show()
        }
    }

    private fun startPhoneNumberVerification(phoneNumber: String) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 120, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD, mCallbacks)
        verificationInProgress = true
    }

    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        signInWithPhoneAuthCredential(credential)
    }

    private fun resendVerificationCode(phoneNumber: String, token: PhoneAuthProvider.ForceResendingToken?) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, activity!!, mCallbacks, token)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth?.signInWithCredential(credential)?.addOnCompleteListener(activity!!) { task ->
            if (task.isSuccessful) {
                updateUI(STATE_SIGN_IN_SUCCESS, task.result?.user)
            } else {
                updateUI(STATE_SIGN_IN_FAILED)
            }
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            updateUI(STATE_SIGN_IN_SUCCESS, user)
        } else {
            updateUI(STATE_INITIALIZED)
        }
    }

    private fun updateUI(uiState: Int, cred: PhoneAuthCredential) {
        updateUI(uiState, null, cred)
    }

    private fun updateUI(uiState: Int, user: FirebaseUser? = auth?.currentUser, cred: PhoneAuthCredential? = null ) {
        when (uiState) {
            STATE_INITIALIZED -> {
                layoutVerification?.let { enableViews(it) }
                layoutVerificationCode?.let { disableViews(it) }
            }
            STATE_CODE_SENT -> {
                dialog.dismiss()
                layoutVerificationCode?.let { enableViews(it) }
                layoutVerification?.let { disableViews(it) }
            }
            STATE_VERIFY_FAILED -> {
                dialog.dismiss()
                Toasty.error(activity!!.applicationContext, "verification failed!", Toast.LENGTH_SHORT, true).show()
            }
            STATE_VERIFY_SUCCESS -> {
                if (cred != null) {
                    if (cred.smsCode != null) {
                        txtVerificationCode?.setText(cred.smsCode)
                    } else {
                        txtVerificationCode?.setText(R.string.instant_validation)
                    }
                }
            }
            STATE_SIGN_IN_FAILED -> {
                dialog.dismiss()
                Toasty.error(activity!!.applicationContext, "verification failed!", Toast.LENGTH_SHORT, true).show()
            }
            STATE_SIGN_IN_SUCCESS -> {
                dialog.dismiss()
                layoutVerification?.let { layoutVerification?.let { it1 -> disableViews(it, it1) } }
                phLogin(user!!)
            }
        }
    }

    private fun enableViews(vararg views: View) {
        for (v in views) {
            v.visibility = View.VISIBLE
            v.isEnabled = true
        }
    }

    private fun disableViews(vararg views: View) {
        for (v in views) {
            v.visibility = View.GONE
            v.isEnabled = false
        }
    }

    private fun mainScreen() {
        dialog.dismiss()
       // Toasty.success(activity!!.applicationContext, "successfully login", Toast.LENGTH_LONG).show()
        val intent = Intent(activity!!.applicationContext, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun phLogin(user: FirebaseUser) {
        loginVM?.login(user)
    }

}