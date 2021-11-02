package com.bd.deliverytiger.app.api.model.instant_payment_status


import com.google.gson.annotations.SerializedName

data class InstantPaymentStatusData(
    @SerializedName("LastPaymentAmount")
    var lastPaymentAmount: Int = 0,
    @SerializedName("LastPaymentStatus")
    var lastPaymentStatus: Int = 0,
    @SerializedName("CurrentPaymentAmount")
    var currentPaymentAmount: Int = 0,
    @SerializedName("PaymentType")
    var paymentType: Int = 0,
    @SerializedName("CurrentPaymentStatus")
    var currentPaymentStatus: Int = 0,
    @SerializedName("LastPaymentDate")
    var lastPaymentDate: String? = "",
    @SerializedName("LastRequestDate")
    var lastRequestDate: String? = "",
    @SerializedName("CurrentRequestDate")
    var currentRequestDate: String? = ""
)