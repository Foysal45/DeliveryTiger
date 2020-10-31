package com.bd.deliverytiger.app.ui.chat.model


import com.google.gson.annotations.SerializedName

data class ChatData(
    @SerializedName("date")
    var date: String? = "",
    @SerializedName("dealID")
    var dealID: String? = "",
    @SerializedName("email")
    var email: String? = "",
    @SerializedName("fileAttached")
    var fileAttached: String? = "",
    @SerializedName("id")
    var id: String? = "",
    @SerializedName("imageUrl")
    var imageUrl: String? = "",
    @SerializedName("key")
    var key: String? = "",
    @SerializedName("message")
    var message: String? = "",
    @SerializedName("name")
    var name: String? = "",
    @SerializedName("userImgUrl")
    var userImgUrl: String? = ""
)