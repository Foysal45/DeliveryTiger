package com.bd.deliverytiger.app.api.model.log_sms


import com.google.gson.annotations.SerializedName

data class SMSLogRequest(
    @SerializedName("bookingId")
    var bookingId: String?,
    @SerializedName("Message")
    var message: String?,
    @SerializedName("orderfrom")
    var orderfrom: String? = "dt",
    @SerializedName("dealId")
    var dealId: String? = "",
    @SerializedName("shopcartId")
    var shopcartId: String? = "",
    @SerializedName("amount")
    var amount: String? = "",
    @SerializedName("paystatus")
    var paystatus: String? = "",
    @SerializedName("cardtype")
    var cardtype: String? = "",
    @SerializedName("validationId")
    var validationId: String? = "",
    @SerializedName("transcationsId")
    var transcationsId: String? = "",
    @SerializedName("status")
    var status: String? = ""
)