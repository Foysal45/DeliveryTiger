package com.bd.deliverytiger.app.api.model.fcm


import com.google.gson.annotations.SerializedName

data class FCMNotification(
    @SerializedName("title")
    var title: String? = "",
    @SerializedName("body")
    var body: String? = "",
    @SerializedName("image")
    var image: String? = ""
)