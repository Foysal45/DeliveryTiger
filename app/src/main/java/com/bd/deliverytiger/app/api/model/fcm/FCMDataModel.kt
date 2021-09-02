package com.bd.deliverytiger.app.api.model.fcm

import com.google.gson.annotations.SerializedName

data class FCMDataModel(
    @SerializedName("title")
    var title: String = "",
    @SerializedName("message")
    var message: String = "",
    @SerializedName("time")
    var time: String = "",
    @SerializedName("name")
    var name: String = "",
    @SerializedName("notificationType")
    var notificationType: String = ""
)

