package com.bd.deliverytiger.app.api.model.charge


import com.google.gson.annotations.SerializedName

data class DeliveryChargeResponse(
    @SerializedName("weightRangeId")
    var weightRangeId: Int,
    @SerializedName("weight")
    var weight: String,
    @SerializedName("weightRangeWiseData")
    var weightRangeWiseData: List<WeightRangeWiseData>,
    @SerializedName("deliveryTypeModel")
    var deliveryTypeModel: List<DeliveryTypeModel>
)