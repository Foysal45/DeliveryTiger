package com.bd.deliverytiger.app.api.model.login

import com.google.gson.annotations.SerializedName

data class SignUpReqBody(
    @SerializedName("mobile")
    var mobile: String,
    @SerializedName("password")
    var password: String
)