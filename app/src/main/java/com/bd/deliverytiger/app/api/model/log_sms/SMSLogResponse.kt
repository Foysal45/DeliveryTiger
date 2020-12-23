package com.bd.deliverytiger.app.api.model.log_sms


import com.google.gson.annotations.SerializedName

data class SMSLogResponse(
    @SerializedName("status")
    var status: Boolean,
    @SerializedName("message")
    var message: String?
)