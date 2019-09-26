package com.bd.deliverytiger.app.api.model.charge


import com.google.gson.annotations.SerializedName

data class DeliveryTypeModel(
    @SerializedName("deliveryType")
    var deliveryType: String,
    @SerializedName("deliveryDayChargeModel")
    var deliveryDayChargeModel: List<DeliveryDayChargeModel>
)