package com.bd.deliverytiger.app.api.model.charge


import com.google.gson.annotations.SerializedName

data class DeliveryChargeRequest(
    @SerializedName("districtId")
    var districtId: Int,
    @SerializedName("thanaId")
    var thanaId: Int,
    @SerializedName("areaId")
    var areaId: Int = 0
)