package com.bd.deliverytiger.app.api.model.fcm

import com.google.gson.annotations.SerializedName

data class FCMRequest(
    @SerializedName("to")
    var to: String = "",
    @SerializedName("notification")
    var notification: FCMNotification = FCMNotification(),
    @SerializedName("data")
    var data: FCMDataModel = FCMDataModel(),
    @SerializedName("time_to_live")
    var timeToLive: Int = 86400
)