package com.bd.deliverytiger.app.api.model.lead_management


import com.google.gson.annotations.SerializedName

data class SMSLogData(
    @SerializedName("courierUserId")
    var courierUserId: Int = 0,
    @SerializedName("mobile")
    var mobile: String? = "",
    @SerializedName("name")
    var name: String? = "",
    @SerializedName("message")
    var message: String? = ""
)