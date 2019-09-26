package com.bd.deliverytiger.app.api.model.charge


import com.google.gson.annotations.SerializedName

data class WeightRangeWiseData(
    @SerializedName("deliveryType")
    var deliveryType: String,
    @SerializedName("weightRangeId")
    var weightRangeId: Int,
    @SerializedName("chargeAmount")
    var chargeAmount: Double,
    @SerializedName("days")
    var days: String
)