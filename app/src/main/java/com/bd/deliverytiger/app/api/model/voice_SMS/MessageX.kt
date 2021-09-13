package com.bd.deliverytiger.app.api.model.voice_SMS


import com.google.gson.annotations.SerializedName

data class MessageX(
    @SerializedName("messageId")
    val messageId: String = "",
    @SerializedName("status")
    val status: Status = Status(),
    @SerializedName("to")
    val to: String = ""
)