package com.bd.deliverytiger.app.api.model.login

import com.google.gson.annotations.SerializedName

data class SignUpReqBody(
    @SerializedName("mobile")
    var mobile: String,
    @SerializedName("password")
    var password: String,
    @SerializedName("referrer")
    var referrer: String = "",
    @SerializedName("bkashNumber")
    var bkashNumber: String = "",
    @SerializedName("preferredPaymentCycle")
    var preferredPaymentCycle: String = "",

    @SerializedName("userName")
    var userName: String = "user name",
    @SerializedName("address")
    var address: String = "",
    @SerializedName("isActive")
    var isActive: Boolean = true
)