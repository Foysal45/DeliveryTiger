package com.bd.deliverytiger.app.api.model.live.share_sms

import com.google.gson.annotations.SerializedName

data class SMSBody(
    @SerializedName("MobileNumber")
    var mobileNumber: String = "",
    @SerializedName("SMSBody")
    var sMSBody: String = ""
)