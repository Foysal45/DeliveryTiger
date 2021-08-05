package com.bd.deliverytiger.app.api.model.live.auth


import com.google.gson.annotations.SerializedName

data class AuthRequestBody(
    @SerializedName("CustomerEmail")
    var customerEmail: String? = "",
    @SerializedName("CustomerMobile")
    var customerMobile: String? = ""
)