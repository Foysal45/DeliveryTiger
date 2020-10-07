package com.bd.deliverytiger.app.api.model.charge


import com.google.gson.annotations.SerializedName

data class DeliveryDayChargeModel(
    @SerializedName("weightRangeId")
    var weightRangeId: Int = 0,
    @SerializedName("deliveryType")
    var deliveryType: String = "",
    @SerializedName("chargeAmount")
    var chargeAmount: Double = 0.0,
    @SerializedName("days")
    var days: String = "",
    @SerializedName("dayType")
    var dayType: String = ""
)