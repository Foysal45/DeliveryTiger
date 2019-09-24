package com.bd.deliverytiger.app.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.NonNull
import androidx.core.content.edit

object SessionManager {

    private val Pref_Name = "com.bd.deliverytiger.app.session"

    private lateinit var pref: SharedPreferences

    private val Is_LOGIN = "isLogIn"
    private val Key_accessToken = "accessTokenKey"
    private val Key_UserId = "userIdKey"
    private val Key_UserName = "userNameKey"
    private val Key_UserEmail = "userEmailKey"
    private val Key_UserPic = "userPicKey"
    private val Key_UserStatus = "userStatusKey"
    private val Key_UserLoginKey = "userLoginKey"
    private val Key_FileBaseUrl = "baseUrlKey"

    fun init(@NonNull context: Context) {
        pref = context.getSharedPreferences(Pref_Name, Context.MODE_PRIVATE)
    }

    fun createSession(
        userId: String?,
        name: String?,
        email: String?,
        pic: String?,
        status: String?,
        loginKey: String?
    ) {

        pref.edit {
            putBoolean(Is_LOGIN, true)
            putString(Key_UserId, userId)
            putString(Key_UserName, name)
            putString(Key_UserEmail, email)
            putString(Key_UserPic, pic)
            putString(Key_UserStatus, status)
            putString(Key_UserLoginKey, loginKey)
        }
    }

    var accessToken: String
        get() {
            return pref.getString(Key_accessToken, "")!!
        }
        set(value) {
            pref.edit {
                putString(Key_accessToken, value)
                commit()
            }
        }

    var isLogin: Boolean
        get() {
            return pref.getBoolean(Is_LOGIN, false)
        }
        set(value) {
            pref.edit {
                putBoolean(Is_LOGIN, value)
                commit()
            }
        }

    var fileBaseUrl: String
    get() {
        return pref.getString(Key_FileBaseUrl, "")!!
    }
    set(value) {
        pref.edit {
            putString(Key_FileBaseUrl, value)
            commit()
        }
    }

}