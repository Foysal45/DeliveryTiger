package com.bd.deliverytiger.app.api.model.profile_update


import com.google.gson.annotations.SerializedName

data class ProfileUpdateReqBody(
    @SerializedName("courierUserId")
    var courierUserId: Int? = 0,
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
    var isSms: Boolean? = false
)