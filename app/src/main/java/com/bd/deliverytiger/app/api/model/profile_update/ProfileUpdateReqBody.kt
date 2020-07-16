package com.bd.deliverytiger.app.api.model.profile_update


import com.google.gson.annotations.SerializedName

data class ProfileUpdateReqBody(
    @SerializedName("courierUserId")
    var courierUserId: Int = 0,
    @SerializedName("companyName")
    var companyName: String? = "",
    @SerializedName("userName")
    var userName: String? = "",
    @SerializedName("mobile")
    var mobile: String? = "",
    @SerializedName("alterMobile")
    var alterMobile: String? = "",
    @SerializedName("emailAddress")
    var emailAddress: String? = "",
    @SerializedName("bkashNumber")
    var bkashNumber: String? = "",
    @SerializedName("address")
    var address: String? = "",
    @SerializedName("isSms")
    var isSms: Boolean = false,
    @SerializedName("fburl")
    var fburl: String? = "",
    @SerializedName("webURL")
    var webURL: String? = "",
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
    var areaName: String? = ""

)