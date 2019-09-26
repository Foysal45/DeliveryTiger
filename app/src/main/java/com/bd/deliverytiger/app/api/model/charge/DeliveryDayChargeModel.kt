package com.bd.deliverytiger.app.api.model.charge


import com.google.gson.annotations.SerializedName

data class DeliveryDayChargeModel(
    @SerializedName("weightRangeId")
    var weightRangeId: Int,
    @SerializedName("deliveryType")
    var deliveryType: String,
    @SerializedName("chargeAmount")
    var chargeAmount: Double,
    @SerializedName("days")
    var days: String
)