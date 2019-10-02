package com.bd.deliverytiger.app.api.model.login


import com.google.gson.annotations.SerializedName

data class OTPRequestModel(
    @SerializedName("CustomerId")
    var customerId: String? = "",
    @SerializedName("MobileOrEmail")
    var mobileOrEmail: String? = "",
    @SerializedName("Type")
    var type: String? = "0"
)