package com.bd.deliverytiger.app.api.model.courier_info


import com.google.gson.annotations.SerializedName

data class AdminUser(
    @SerializedName("userId")
    var userId: Int = 0,
    @SerializedName("userName")
    var userName: String? = "",
    @SerializedName("fullName")
    var fullName: String? = "",
    @SerializedName("adminType")
    var adminType: Int = 0,
    @SerializedName("passwrd")
    var passwrd: String? = "",
    @SerializedName("isActive")
    var isActive: Int = 0,
    @SerializedName("token")
    var token: String? = "",
    @SerializedName("mobile")
    var mobile: String? = ""
)