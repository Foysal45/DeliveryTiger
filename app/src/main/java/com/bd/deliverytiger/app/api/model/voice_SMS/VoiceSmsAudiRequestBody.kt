package com.bd.deliverytiger.app.api.model.voice_SMS


import com.google.gson.annotations.SerializedName

data class VoiceSmsAudiRequestBody(
    @SerializedName("messages")
    val messages: List<Message> = listOf()
)