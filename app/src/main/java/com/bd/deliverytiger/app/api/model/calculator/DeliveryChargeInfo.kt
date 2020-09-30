package com.bd.deliverytiger.app.api.model.calculator


import com.google.gson.annotations.SerializedName

data class DeliveryChargeInfo(
    @SerializedName("codCharge")
    var codCharge: String? = "",
    @SerializedName("inSideDhaka")
    var inSideDhaka: DeliveryInfo = DeliveryInfo(),
    @SerializedName("outSideDhaka")
    var outSideDhaka: DeliveryInfo = DeliveryInfo()
)