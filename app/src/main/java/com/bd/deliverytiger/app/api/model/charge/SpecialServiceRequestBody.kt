package com.bd.deliverytiger.app.api.model.charge


import com.google.gson.annotations.SerializedName

data class SpecialServiceRequestBody(
    @SerializedName("merchantId")
    var merchantId: Int = 0,
    @SerializedName("districtId")
    var districtId: Int = 0,
    @SerializedName("thanaId")
    var thanaId: Int = 0,
    @SerializedName("areaId")
    var areaId: Int = 0
)