package com.bd.deliverytiger.app.api.model.fcm

import com.google.gson.annotations.SerializedName

data class FCMDataModel(
    @SerializedName("notificationType")
    var notificationType: String = "",
    @SerializedName("title")
    var title: String = "",
    @SerializedName("body")
    var body: String = "",
    @SerializedName("time")
    var time: String = "",
    @SerializedName("senderId")
    var senderId: String = "",
    @SerializedName("senderName")
    var senderName: String = "",
    @SerializedName("senderRole")
    var senderRole: String = "",
    @SerializedName("receiverId")
    var receiverId: String = ""

)

