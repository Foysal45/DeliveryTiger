package com.bd.deliverytiger.app.api.model.calculator


import com.google.gson.annotations.SerializedName

data class WeightPrice(
    @SerializedName("courierDeliveryCharge")
    var courierDeliveryCharge: Int = 0,
    @SerializedName("weightNumber")
    var weightNumber: Int = 0
)