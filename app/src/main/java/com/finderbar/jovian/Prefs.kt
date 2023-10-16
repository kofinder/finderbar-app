package com.finderbar.jovian

import android.content.Context
import android.content.SharedPreferences


/**
 * Created by FinderBar
 */

class Prefs (context: Context) {
    companion object {
        private const val PREFS_FILENAME = "com.finderbar.finderpp.prefs"
        private const val USER_ID = "userId";
        private const val USER_TOKEN = "authToken"
        private const val FULL_NAME = "fullName"
        private const val PASSWORD = "password"
        private const val AVATAR = "avatar"
        private const val AUTH_CODE = "authCode"
        private const val PROVIDER = "provider"
        private const val NOTIFICATION_BADGE_COUNT = "badgeCount";
        private const val MENU_NOTIFICATION_BADGE_COUNT = "menuBadgeCount";
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0)
    var editor = prefs.edit()


    var userId: String
        get() = prefs.getString(USER_ID, "")
        set(value) = prefs.edit().putString(USER_ID, value).apply()

    var fullName: String
        get() = prefs.getString(FULL_NAME, "")
        set(value) = prefs.edit().putString(FULL_NAME, value).apply()

    var avatar: String
        get() = prefs.getString(AVATAR, "")
        set(value) = prefs.edit().putString(AVATAR, value).apply()

    var password: String
        get() = prefs.getString(PASSWORD, "")
        set(value) = prefs.edit().putString(PASSWORD, value).apply()

    var authToken: String
        get() = prefs.getString(USER_TOKEN, "")
        set(value) = prefs.edit().putString(USER_TOKEN, value).apply()

    var authCode: String
        get() = prefs.getString(AUTH_CODE, "")
        set(value) = prefs.edit().putString(AUTH_CODE, value).apply()

    var provider: String
        get() = prefs.getString(PROVIDER, "")
        set(value) = prefs.edit().putString(PROVIDER, value).apply()

    var badgeCount: String
        get() = prefs.getString(NOTIFICATION_BADGE_COUNT, "0")
        set(value) = prefs.edit().putString(NOTIFICATION_BADGE_COUNT, value).apply()

    var menuBadgeCount: String
        get() = prefs.getString(MENU_NOTIFICATION_BADGE_COUNT, "0")
        set(value) = prefs.edit().putString(MENU_NOTIFICATION_BADGE_COUNT, value).apply()


    var auth: Auth
        get() = Auth(userId, authToken, fullName, avatar, provider)
        set(value) {
            userId = value.userId
            authToken = value.authToken
            fullName = value.fullName
            avatar = value.avatar
            provider = value.provider
        }

    fun clearTmpData() {
        prefs.edit().remove(PROVIDER)
        prefs.edit().remove(AUTH_CODE)
        prefs.edit().remove(PASSWORD);
        prefs.edit().commit()
    }

    fun clearBadgeCount() {
        prefs.edit().remove(NOTIFICATION_BADGE_COUNT)
        prefs.edit().commit()
    }

    fun logout() {
        clearTmpData()
        clearBadgeCount()
        editor.clear()
        editor.commit()
    }

}