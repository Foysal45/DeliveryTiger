package com.bd.deliverytiger.app.api.model.login


import com.google.gson.annotations.SerializedName

data class OTPResponse(
    @SerializedName("MessageCode")
    var messageCode: Int = 0,
    @SerializedName("MessageText")
    var messageText: String? = "",
    @SerializedName("DatabseTracking")
    var databseTracking: Boolean = false,
    @SerializedName("Data")
    var model: String? = ""
)