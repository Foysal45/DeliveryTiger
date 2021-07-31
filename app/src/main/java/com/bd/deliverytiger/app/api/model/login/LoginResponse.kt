package com.bd.deliverytiger.app.api.model.login


import com.bd.deliverytiger.app.api.model.courier_info.AdminUser
import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("courierUserId")
    var courierUserId: Int,
    @SerializedName("userName")
    var userName: String,
    @SerializedName("companyName")
    var companyName: String?,
    @SerializedName("password")
    var password: String?,
    @SerializedName("token")
    var token: String,
    @SerializedName("refreshToken")
    var refreshToken: String,
    @SerializedName("mobile")
    var mobile: String,
    @SerializedName("isActive")
    var isActive: Boolean,
    @SerializedName("address")
    var address: String,
    @SerializedName("collectionCharge")
    var collectionCharge: Double,
    @SerializedName("returnCharge")
    var returnCharge: Double,
    @SerializedName("smsCharge")
    var smsCharge: Double,
    @SerializedName("mailCharge")
    var mailCharge: Double,
    @SerializedName("sms")
    var sms: Boolean,
    @SerializedName("email")
    var email: Boolean,
    @SerializedName("emailAddress")
    var emailAddress: String,
    @SerializedName("bkashNumber")
    var bkashNumber: String,
    @SerializedName("alterMobile")
    var alterMobile: String,
    @SerializedName("maxCodCharge")
    var maxCodCharge: Double,
    @SerializedName("credit")
    var credit: Double,
    @SerializedName("fburl")
    var fburl: String,
    @SerializedName("webURL")
    var webURL: String,
    @SerializedName("districtId")
    var districtId: Int = 0,
    @SerializedName("thanaId")
    var thanaId: Int = 0,
    @SerializedName("areaId")
    var areaId: Int = 0,
    @SerializedName("districtName")
    var districtName: String? = "",
    @SerializedName("thanaName")
    var thanaName: String? = "",
    @SerializedName("areaName")
    var areaName: String? = "",
    @SerializedName("adminUsers")
    var adminUsers: AdminUser? = AdminUser()
)