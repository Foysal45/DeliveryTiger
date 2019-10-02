package com.bd.deliverytiger.app.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.NonNull
import androidx.core.content.edit
import com.bd.deliverytiger.app.api.model.login.LoginResponse
import com.bd.deliverytiger.app.api.model.order.UpdateOrderReqBody
import com.bd.deliverytiger.app.api.model.profile_update.ProfileUpdateReqBody

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

    fun createSession(model: LoginResponse) {

        pref.edit {
            putBoolean(Is_LOGIN, true)
            putInt(Key_UserId, model.courierUserId)
            putString(Key_UserName, model.userName)
            putString("companyName", model.companyName)
            putString(Key_accessToken, model.token)
            putString("refreshToken", model.refreshToken)
            putString("mobile", model.mobile)
            putString("address", model.address)
            putString("emailAddress", model.emailAddress)
            putString("bkashNumber", model.bkashNumber)
            putString("alterMobile", model.alterMobile)
            putBoolean("isActive", model.isActive)
            putBoolean("sms", model.sms)
            putBoolean("email", model.email)
            putFloat("collectionCharge", model.collectionCharge.toFloat())
            putFloat("returnCharge", model.returnCharge.toFloat())
            putFloat("smsCharge", model.smsCharge.toFloat())
            putFloat("mailCharge", model.mailCharge.toFloat())
            putFloat("maxCodCharge", model.maxCodCharge.toFloat())
        }
    }

    fun clearSession() {
        pref.edit {
            clear()
            commit()
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

    var refreshToken: String
        get() {
            return pref.getString("refreshToken", "")!!
        }
        set(value) {
            pref.edit {
                putString("refreshToken", value)
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

    var collectionCharge: Double
        get() {
            return pref.getFloat("collectionCharge", 0f).toDouble()
        }
        set(value) {
            pref.edit {
                putFloat("collectionCharge", value.toFloat())
                commit()
            }
        }

    var returnCharge: Double
        get() {
            return pref.getFloat("returnCharge", 0f).toDouble()
        }
        set(value) {
            pref.edit {
                putFloat("returnCharge", value.toFloat())
                commit()
            }
        }

    var maxCodCharge: Double
        get() {
            return pref.getFloat("maxCodCharge", 0f).toDouble()
        }
        set(value) {
            pref.edit {
                putFloat("maxCodCharge", value.toFloat())
                commit()
            }
        }

    var courierUserId: Int
        get() {
            return pref.getInt(Key_UserId, 0)
        }
        set(value) {
            pref.edit {
                putInt(Key_UserId, value)
                commit()
            }
        }

    var userName: String
        get() {
            return pref.getString(Key_UserName, "")!!
        }
        set(value) {
            pref.edit {
                putString(Key_UserName, value)
                commit()
            }
        }

    var companyName: String
        get() {
            return pref.getString("companyName", "")!!
        }
        set(value) {
            pref.edit {
                putString("companyName", value)
                commit()
            }
        }

    var mobile: String
        get() {
            return pref.getString("mobile", "")!!
        }
        set(value) {
            pref.edit {
                putString("mobile", value)
                commit()
            }
        }

    var address: String
        get() {
            return pref.getString("address", "")!!
        }
        set(value) {
            pref.edit {
                putString("address", value)
                commit()
            }
        }

    var alterMobile: String
        get() {
            return pref.getString("alterMobile", "")!!
        }
        set(value) {
            pref.edit {
                putString("alterMobile", value)
                commit()
            }
        }
    var versionName: String
        get() {
            return pref.getString("appVersionName", "")!!
        }
        set(value) {
            pref.edit {
                putString("appVersionName", value)
                commit()
            }
        }

    fun getSessionData(): ProfileUpdateReqBody {
        val model = ProfileUpdateReqBody(pref.getInt(Key_UserId,0), pref.getString(Key_UserName,""),pref.getString("mobile",""),pref.getString("alterMobile",""),pref.getString("emailAddress",""),
            pref.getString("bkashNumber",""),pref.getString("address",""),pref.getBoolean("sms",false))
        return model
    }

    fun updateSession(model: ProfileUpdateReqBody) {
        pref.edit {
            putString(Key_UserName, model.userName)
            putString("mobile", model.mobile)
            putString("address", model.address)
            putString("emailAddress", model.emailAddress)
            putString("bkashNumber", model.bkashNumber)
            putString("alterMobile", model.alterMobile)
            putBoolean("sms", model.isSms!!)
            putString("emailAddress", model.emailAddress)
        }
    }


}