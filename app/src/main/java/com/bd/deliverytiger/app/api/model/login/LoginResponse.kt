package com.bd.deliverytiger.app.api.model.login


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
    var maxCodCharge: Double
)