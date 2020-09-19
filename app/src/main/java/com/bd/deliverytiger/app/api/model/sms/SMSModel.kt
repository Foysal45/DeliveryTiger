package com.bd.deliverytiger.app.api.model.sms


import com.google.gson.annotations.SerializedName

data class SMSModel(
    @SerializedName("numbers")
    var numbers: List<String> = listOf(),
    @SerializedName("text")
    var text: String = "",
    @SerializedName("type")
    var type: Int = 0,
    @SerializedName("datacoding")
    var datacoding: Int = 0
)