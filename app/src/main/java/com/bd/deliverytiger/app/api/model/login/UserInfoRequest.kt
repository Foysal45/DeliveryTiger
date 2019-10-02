package com.bd.deliverytiger.app.api.model.login


import com.google.gson.annotations.SerializedName

data class UserInfoRequest(
    @SerializedName("mobile")
    var mobile: String? = ""
)