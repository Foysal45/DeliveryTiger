package com.bd.deliverytiger.app.api.model


import com.google.gson.annotations.SerializedName

data class ResponseHeader<T> (
    @SerializedName("MessageCode")
    var messageCode: Int = 0,
    @SerializedName("MessageText")
    var messageText: String? = "",
    @SerializedName("DatabseTracking")
    var databaseTracking: Boolean = false,
    @SerializedName("Data")
    var `data`: T?
)