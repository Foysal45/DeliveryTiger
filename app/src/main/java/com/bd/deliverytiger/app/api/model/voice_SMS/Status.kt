package com.bd.deliverytiger.app.api.model.voice_SMS


import com.google.gson.annotations.SerializedName

data class Status(
    @SerializedName("description")
    val description: String = "",
    @SerializedName("groupId")
    val groupId: Int = 0,
    @SerializedName("groupName")
    val groupName: String = "",
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("name")
    val name: String = ""
)