package com.bd.deliverytiger.app.api.model.sms


import com.google.gson.annotations.SerializedName

data class SMSResponse(
    @SerializedName("Status")
    var status: Boolean = false,
    @SerializedName("Number")
    var number: String? = ""
)