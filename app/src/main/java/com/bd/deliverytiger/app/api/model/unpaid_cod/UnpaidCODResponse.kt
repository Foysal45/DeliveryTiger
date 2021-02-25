package com.bd.deliverytiger.app.api.model.unpaid_cod


import com.google.gson.annotations.SerializedName

data class UnpaidCODResponse(
    @SerializedName("NetAdjustedAmount")
    var netAdjustedAmount: Int = 0,
    @SerializedName("ReceivableOrderCount")
    var receivableOrderCount: Int = 0,
    @SerializedName("PayableOrderCount")
    var payableOrderCount: Int = 0,
    @SerializedName("TotalOrderCount")
    var totalOrderCount: Int = 0,
    @SerializedName("TotalAdvAccReceiveable")
    var totalAdvAccReceiveable: Int = 0,
    @SerializedName("AdvAccReceiveableOrderCount")
    var advAccReceiveableOrderCount: Int = 0,
    @SerializedName("TotalMerchantReceivable")
    var totalMerchantReceivable: Int = 0,
    @SerializedName("TotalCodServiceCharge")
    var totalCodServiceCharge: Int = 0,
    @SerializedName("TotalCollectedAmount")
    var totalCollectedAmount: Int = 0,
    @SerializedName("TotalMerchantPayable")
    var totalMerchantPayable: Int = 0,

    @SerializedName("Availability")
    var availability: Boolean = false,
    @SerializedName("AvailabilityMessage")
    var availabilityMessage: String = "",

    @SerializedName("PayableOrders")
    var payableOrders: List<CODDetailsData> = listOf(),
    @SerializedName("ReceivableOrders")
    var receivableOrders: List<CODDetailsData> = listOf(),
    @SerializedName("AdvAccReceiveableOrders")
    var advAccReceiveableOrders: List<CODDetailsData> = listOf()
)