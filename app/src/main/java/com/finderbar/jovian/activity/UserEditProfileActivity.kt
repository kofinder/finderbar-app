package com.finderbar.jovian.activity

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.Menu
import com.finderbar.jovian.R
import kotlinx.android.synthetic.main.activity_user_edit_profile.*
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.*
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.finderbar.jovian.prefs
import com.finderbar.jovian.viewmodels.user.UserProfileVM
import es.dmoral.toasty.Toasty
import okhttp3.MediaType
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback
import java.io.File
import okhttp3.MultipartBody
import okhttp3.RequestBody
import pub.devrel.easypermissions.EasyPermissions
import android.net.Uri
import android.provider.MediaStore
import com.bumptech.glide.request.RequestOptions
import com.finderbar.jovian.models.MyFile
import com.finderbar.jovian.utilities.api.ApiClientUtil
import com.finderbar.jovian.utilities.android.GlideApp
import com.finderbar.jovian.utilities.android.loadAvatar
import pub.devrel.easypermissions.AppSettingsDialog


class UserEditProfileActivity : AppCompatActivity(), View.OnClickListener, EasyPermissions.PermissionCallbacks {

    private var imageRequest = 1
    private var profileImageView: ImageView ? = null;
    private var txtFullName: TextView? = null;
    private var txtBirthday: TextView? = null;
    private var txtLanguage: TextView? = null;
    private var txtNationality: TextView? = null;
    private var txtFacebook: TextView? = null;
    private var txtWorkPhone: TextView? = null;
    private var txtHandPhone: TextView? = null;
    private var txtAddress: TextView? = null;
    private var toolbar: Toolbar? = null
    private var rdoSexBtnGroup : RadioGroup? = null;
    private var rdoRelBtnGroup : RadioGroup? = null;
    private var rdoMaleBtn: RadioButton? = null;
    private var rdoFeMaleBtn: RadioButton? = null;
    private var rdoSingleBtn: RadioButton? = null;
    private var rdoMarriedBtn: RadioButton? = null;
    private var sexRdo : RadioButton? = null;
    private var relRdo : RadioButton? = null;
    private var avatarUrl = "https://finderresources.s3-ap-southeast-1.amazonaws.com/avatar.png";

    private lateinit var dialog: ACProgressFlower
    private lateinit var userProfileVM: UserProfileVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_edit_profile)

        toolbar = findViewById(R.id.toolbar)
        toolbar!!.title = "Edit Profile"
        setSupportActionBar(toolbar);
        supportActionBar!!.setDisplayHomeAsUpEnabled(true);

        userProfileVM = ViewModelProviders.of(this).get(UserProfileVM::class.java);
        userProfileVM.getUserProfile(prefs.userId);


        dialog = ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .fadeColor(Color.LTGRAY).build()

        profileImageView = findViewById(R.id.profileImg);
        txtFullName = findViewById(R.id.usr_full_name);
        rdoMaleBtn = findViewById(R.id.rdo_male)
        rdoFeMaleBtn = findViewById(R.id.rdo_female)
        rdoSingleBtn = findViewById(R.id.rdo_single)
        rdoMarriedBtn = findViewById(R.id.rdo_marry)
        rdoSexBtnGroup = findViewById(R.id.rdo_gender)
        rdoRelBtnGroup = findViewById(R.id.rdo_rls);
        txtBirthday = findViewById(R.id.usr_birthday);
        txtLanguage = findViewById(R.id.usr_language);
        txtNationality = findViewById(R.id.usr_nal);
        txtFacebook = findViewById(R.id.usr_fb);
        txtWorkPhone = findViewById(R.id.usr_workphone);
        txtHandPhone = findViewById(R.id.usr_handphone);
        txtAddress = findViewById(R.id.usr_addr);

        userProfileVM.userProfile!!.observe(this, Observer {
            avatarUrl = it!!.avatar.toString()
            txtFullName!!.text = it!!.userName;
            txtBirthday!!.text = it!!.birthday;
            txtLanguage!!.text = it!!.language;
            txtNationality!!.text = it!!.nationality;
            txtFacebook!!.text = it!!.facebook;
            txtWorkPhone!!.text = it!!.workPhone;
            txtHandPhone!!.text = it!!.handPhone;
            txtAddress!!.text = it!!.address;
            profileImg.loadAvatar(Uri.parse(avatarUrl))

            if(it!!.gender == "Male"){
                rdoMaleBtn!!.isChecked = true
                rdoFeMaleBtn!!.isChecked = false
            }
            else {
                rdoMaleBtn!!.isChecked = false
                rdoFeMaleBtn!!.isChecked = true
            }
            if(it!!.relationship.equals("Single")){
                rdoSingleBtn!!.isChecked = true
                rdoMarriedBtn!!.isChecked = false
            }
            else {
                rdoSingleBtn!!.isChecked = false
                rdoMarriedBtn!!.isChecked = true
            }
        })

        profileImg.setOnClickListener(this)

        btn_profile_save.setOnClickListener{
            dialog.show()
            val fullName = usr_full_name.text.toString();
            sexRdo = findViewById(rdoSexBtnGroup!!.checkedRadioButtonId);
            relRdo = findViewById(rdoRelBtnGroup!!.checkedRadioButtonId);
            val gender = sexRdo!!.text.toString();
            val relationship = relRdo!!.text.toString();
            val birthday = usr_birthday.text.toString();
            val language = usr_language.text.toString();
            val nationality = usr_nal.text.toString();
            val facebook = usr_fb.text.toString();
            val workPhone = usr_workphone.text.toString();
            val handPhone = usr_handphone.text.toString();
            val address = usr_addr.text.toString();
            userProfileVM.modifyUserProfile(prefs.userId, fullName, avatarUrl,  gender, relationship, birthday,
                    language, nationality, facebook, workPhone, handPhone, address);

            userProfileVM.modifyMessage!!.observe(this, Observer {
                dialog.dismiss();
                Toasty.success(this, it!!.message, Toast.LENGTH_SHORT, true).show();
                val intent = Intent(this@UserEditProfileActivity, UserProfileActivity::class.java)
                intent.putExtra("userId", prefs.userId)
                startActivity(intent)
                finish();
            });

            userProfileVM.errorMessage!!.observe(this, Observer {
                dialog.dismiss();
                Toasty.error(this, it!!.message, Toast.LENGTH_SHORT, true).show();
            })
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit_profile, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.p_edit ->  {
                startActivity(Intent(this@UserEditProfileActivity, UserEditProfileActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onClick(v: View) {
        if(EasyPermissions.hasPermissions(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)){
            val pickIntent = Intent(Intent.ACTION_PICK);
            pickIntent.type = "tutoHeader/*";
            startActivityForResult(pickIntent, imageRequest);
        } else {
            EasyPermissions.requestPermissions(this,"This application need your permission to access photo gallery.",991, android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == imageRequest && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
//            val filePath = getRealPathFromURIPath(data.data, this);
//            val file = File(filePath);
//            val mFile = RequestBody.create(MediaType.parse("tutoHeader/*"), file);
//            val imgName = MultipartBody.Part.createFormData("file", file.name, mFile);
//            uploadToServer(imgName)
//        }

    }

    private fun uploadToServer(imgName: MultipartBody.Part) {
        dialog.show()
        val header = mutableMapOf("x-auth-id" to prefs.userId, "x-auth-token" to prefs.authToken);
        val call = ApiClientUtil.fileUploader().fileUpload(header, imgName);
        call.enqueue(object : Callback<MyFile> {
            override fun onFailure(call: Call<MyFile>, t: Throwable) {
                dialog.dismiss()
                Toasty.error(applicationContext, t.message.toString(), Toast.LENGTH_SHORT, true).show();
            }
            override fun onResponse(call: Call<MyFile>, response: Response<MyFile>) {
                avatarUrl = response.body()!!.imgUrl;
                prefs.avatar = avatarUrl
                profileImg.loadAvatar(Uri.parse(avatarUrl))
                Toasty.success(applicationContext, "upload completed!", Toast.LENGTH_SHORT, true).show();
                dialog.dismiss()
            }
        })
    }

    private fun getRealPathFromURIPath(contentURI: Uri, activity: Activity): String? {
        val cursor = activity.contentResolver.query(contentURI, null, null, null, null);
        return if (cursor == null) {
            contentURI.path
        } else {
            cursor.moveToFirst();
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            cursor.getString(idx);
        }
    }


    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {}
    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
           AppSettingsDialog.Builder(this).build().show();
        }
    }

}
