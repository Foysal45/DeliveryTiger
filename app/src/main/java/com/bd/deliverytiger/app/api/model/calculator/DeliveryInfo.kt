package com.bd.deliverytiger.app.api.model.calculator


import com.google.gson.annotations.SerializedName

data class DeliveryInfo(
    @SerializedName("text")
    var text: String? = "",
    @SerializedName("districtId")
    var districtId: Int = 0,
    @SerializedName("deliveryRange")
    var deliveryRange: List<DeliveryType> = listOf()
)