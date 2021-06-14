package com.bd.deliverytiger.app.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.NonNull
import androidx.core.content.edit
import com.bd.deliverytiger.app.api.model.login.LoginResponse
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

            putString("fbUrl", model.fburl)
            putString("webUrl", model.webURL)
            putInt("districtId", model.districtId)
            putInt("thanaId", model.thanaId)
            putInt("areaId", model.areaId)
            putString("districtName", model.districtName)
            putString("thanaName", model.thanaName)
            putString("areaName", model.areaName)
            putInt("credit", model.credit.toInt())
        }
    }

    fun clearSession() {
        pref.edit {
            clear()
        }
    }

    var accessToken: String
        get() {
            return pref.getString(Key_accessToken, "")!!
        }
        set(value) {
            pref.edit {
                putString(Key_accessToken, value)
            }
        }

    var refreshToken: String
        get() {
            return pref.getString("refreshToken", "")!!
        }
        set(value) {
            pref.edit {
                putString("refreshToken", value)
            }
        }

    var firebaseToken: String
        get() {
            return pref.getString("firebaseToken", "")!!
        }
        set(value) {
            pref.edit {
                putString("firebaseToken", value)
            }
        }

    var deviceId: String
        get() {
            return pref.getString("deviceId", "")!!
        }
        set(value) {
            pref.edit {
                putString("deviceId", value)
            }
        }

    var isLogin: Boolean
        get() {
            return pref.getBoolean(Is_LOGIN, false)
        }
        set(value) {
            pref.edit {
                putBoolean(Is_LOGIN, value)
            }
        }

    var isRememberMe: Boolean
        get() {
            return pref.getBoolean("isRememberMe", false)
        }
        set(value) {
            pref.edit {
                putBoolean("isRememberMe", value)
            }
        }

    var loginId: String
        get() {
            return pref.getString("loginID", "")!!
        }
        set(value) {
            pref.edit {
                putString("loginID", value)
            }
        }

    var loginPassword: String
        get() {
            return pref.getString("loginPassword", "")!!
        }
        set(value) {
            pref.edit {
                putString("loginPassword", value)
            }
        }

    var fileBaseUrl: String
        get() {
            return pref.getString(Key_FileBaseUrl, "")!!
        }
        set(value) {
            pref.edit {
                putString(Key_FileBaseUrl, value)
            }
        }

    var collectionCharge: Double
        get() {
            return pref.getFloat("collectionCharge", 0f).toDouble()
        }
        set(value) {
            pref.edit {
                putFloat("collectionCharge", value.toFloat())
            }
        }

    var returnCharge: Double
        get() {
            return pref.getFloat("returnCharge", 0f).toDouble()
        }
        set(value) {
            pref.edit {
                putFloat("returnCharge", value.toFloat())
            }
        }

    var maxCodCharge: Double
        get() {
            return pref.getFloat("maxCodCharge", 0f).toDouble()
        }
        set(value) {
            pref.edit {
                putFloat("maxCodCharge", value.toFloat())
            }
        }

    var courierUserId: Int
        get() {
            return pref.getInt(Key_UserId, 0)
        }
        set(value) {
            pref.edit {
                putInt(Key_UserId, value)
            }
        }

    var userName: String
        get() {
            return pref.getString(Key_UserName, "")!!
        }
        set(value) {
            pref.edit {
                putString(Key_UserName, value)
            }
        }

    var companyName: String
        get() {
            return pref.getString("companyName", "")!!
        }
        set(value) {
            pref.edit {
                putString("companyName", value)
            }
        }

    var mobile: String
        get() {
            return pref.getString("mobile", "")!!
        }
        set(value) {
            pref.edit {
                putString("mobile", value)
            }
        }

    var bkashNumber: String
        get() {
            return pref.getString("bkashNumber", "")!!
        }
        set(value) {
            pref.edit {
                putString("bkashNumber", value)
            }
        }

    var credit: Int
        get() {
            return pref.getInt("credit", 0)
        }
        set(value) {
            pref.edit {
                putInt("credit", value)
            }
        }

    var netAmount: Int
        get() {
            return pref.getInt("netAdjustedAmount", 0)
        }
        set(value) {
            pref.edit {
                putInt("netAdjustedAmount", value)
            }
        }

    var address: String
        get() {
            return pref.getString("address", "")!!
        }
        set(value) {
            pref.edit {
                putString("address", value)
            }
        }

    var alterMobile: String
        get() {
            return pref.getString("alterMobile", "")!!
        }
        set(value) {
            pref.edit {
                putString("alterMobile", value)
            }
        }
    var versionName: String
        get() {
            return pref.getString("appVersionName", "")!!
        }
        set(value) {
            pref.edit {
                putString("appVersionName", value)
            }
        }

    var profileImgUri: String
        get() {
            return pref.getString("profile_img", "").toString()
        }
        set(value) {
            pref.edit {
                putString("profile_img", value)
            }
        }

    fun getSessionData(): ProfileUpdateReqBody {
        return ProfileUpdateReqBody(
            pref.getInt(Key_UserId, 0),
            pref.getString("companyName", ""),
            pref.getString(Key_UserName, ""),
            pref.getString("mobile", ""),
            pref.getString("alterMobile", ""),
            pref.getString("emailAddress", ""),
            pref.getString("bkashNumber", ""),
            pref.getString("address", ""),
            pref.getBoolean("sms", false),

            pref.getString("fbUrl", ""),
            pref.getString("webUrl", ""),
            pref.getInt("districtId", 0),
            pref.getInt("thanaId", 0),
            pref.getInt("areaId", 0),
            pref.getString("districtName", ""),
            pref.getString("thanaName", ""),
            pref.getString("areaName", "")
        )
    }

    fun updateSession(model: ProfileUpdateReqBody) {
        pref.edit {
            putString("companyName", model.companyName)
            putString(Key_UserName, model.userName)
            putString("mobile", model.mobile)
            putString("address", model.address)
            putString("emailAddress", model.emailAddress)
            putString("bkashNumber", model.bkashNumber)
            putString("alterMobile", model.alterMobile)
            putBoolean("sms", model.isSms!!)
            putString("emailAddress", model.emailAddress)


            putString("fbUrl", model.fburl)
            putString("webUrl", model.webURL)
            putInt("districtId", model.districtId)
            putInt("thanaId", model.thanaId)
            putInt("areaId", model.areaId)
            putString("districtName", model.districtName)
            putString("thanaName", model.thanaName)
            putString("areaName", model.areaName)
        }
    }

    var isPickupLocationAdded: Boolean
        get() {
            return pref.getBoolean("isPickupLocationAdded", false)
        }
        set(value) {
            pref.edit {
                putBoolean("isPickupLocationAdded", value)
            }
        }

    var popupShowCount: Int
        get() {
            return pref.getInt("popupShowCount", 0)
        }
        set(value) {
            pref.edit {
                putInt("popupShowCount", value)
            }
        }

    var popupDateOfYear: Int
        get() {
            return pref.getInt("popupDate", 0)
        }
        set(value) {
            pref.edit {
                putInt("popupDate", value)
            }
        }

    var isCollectorAttendance: Boolean
        get() {
            return pref.getBoolean("collectorAttendance", false)
        }
        set(value) {
            pref.edit {
                putBoolean("collectorAttendance", value)
            }
        }

    var collectorAttendanceDateOfYear: Int
        get() {
            return pref.getInt("AttendanceDateOfYear", 0)
        }
        set(value) {
            pref.edit {
                putInt("AttendanceDateOfYear", value)
            }
        }

    var orderSource: String
        get() {
            return pref.getString("orderSource", "")!!
        }
        set(value) {
            pref.edit {
                putString("orderSource", value)
            }
        }

    var totalAmount: Int
        get() {
            return pref.getInt("totalAmount", 0)
        }
        set(value) {
            pref.edit {
                putInt("totalAmount", value)
            }
        }

    var instantPaymentLastRequestDate: String
        get() {
            return pref.getString("lastRequestDate", "")!!
        }
        set(value) {
            pref.edit {
                putString("lastRequestDate", value)
            }
        }

    var instantPaymentLastStatus: Int
        get() {
            return pref.getInt("instantPaymentLastStatus", -1)
        }
        set(value) {
            pref.edit {
                putInt("instantPaymentLastStatus", value)
            }
        }

    var instantPaymentAmount: Int
        get() {
            return pref.getInt("instantPaymentAmount", 0)
        }
        set(value) {
            pref.edit {
                putInt("instantPaymentAmount", value)
            }
        }

    var retentionManagerName: String
        get() {
            return pref.getString("retentionManagerName", "")!!
        }
        set(value) {
            pref.edit {
                putString("retentionManagerName", value)
            }
        }

    var retentionManagerNumber: String
        get() {
            return pref.getString("retentionManagerNumber", "")!!
        }
        set(value) {
            pref.edit {
                putString("retentionManagerNumber", value)
            }
        }
}