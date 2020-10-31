package com.bd.deliverytiger.app.ui.chat.model


import com.google.gson.annotations.SerializedName

data class ChatHistoryData(
    @SerializedName("customerId")
    var customerId: String? = "",
    @SerializedName("customerImgUrl")
    var customerImgUrl: String? = "",
    @SerializedName("customerName")
    var customerName: String? = "",
    @SerializedName("key")
    var key: String? = "",
    @SerializedName("lastMsg")
    var lastMsg: String? = "",
    @SerializedName("seenStatus")
    var seenStatus: String? = "",
    @SerializedName("time")
    var time: String? = "",
    @SerializedName("timeForSort")
    var timeForSort: Long = 0
)