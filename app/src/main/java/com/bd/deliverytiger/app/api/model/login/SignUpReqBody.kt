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
    @SerializedName("knowingSource")
    var knowingSource: String = "",
    @SerializedName("companyName")
    var companyName: String = "company name",

    @SerializedName("accountName")
    var accountName: String = "",
    @SerializedName("accountNumber")
    var accountNumber: String = "",
    @SerializedName("bankName")
    var bankName: String = "",
    @SerializedName("branchName")
    var branchName: String = "",
    @SerializedName("routingNumber")
    var routingNumber: String = "",
    @SerializedName("gender")
    var gender: String = "",
    @SerializedName("fburl")
    var fbUrl: String? = "",

    @SerializedName("registrationFrom")
    var registrationFrom: String = "app",
    @SerializedName("userName")
    var userName: String = "user name",
    @SerializedName("address")
    var address: String = "",
    @SerializedName("isActive")
    var isActive: Boolean = true
)