package com.finderbar.jovian
import android.text.TextUtils


data class Auth(val userId: String = "", var authToken: String = "", var fullName: String = "", var avatar: String = "", var provider: String = "" ) {
    fun isLogin(): Boolean = !TextUtils.isEmpty(userId) || !TextUtils.isEmpty(authToken) || !TextUtils.isEmpty(fullName)
}
