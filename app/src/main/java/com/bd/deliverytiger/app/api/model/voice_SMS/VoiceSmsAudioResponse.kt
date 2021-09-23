package com.bd.deliverytiger.app.api.model.voice_SMS


import com.google.gson.annotations.SerializedName

data class VoiceSmsAudioResponse(
    @SerializedName("bulkId")
    val bulkId: String = "",
    @SerializedName("messages")
    val messages: List<MessageX> = listOf()
)