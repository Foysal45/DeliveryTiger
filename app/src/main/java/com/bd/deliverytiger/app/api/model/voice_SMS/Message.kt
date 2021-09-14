package com.bd.deliverytiger.app.api.model.voice_SMS


import com.google.gson.annotations.SerializedName

data class Message(
    @SerializedName("audioFileUrl")
    val audioFileUrl: String = "",
    @SerializedName("from")
    val from: String = "",
    @SerializedName("to")
    val to: List<String> = listOf()
)