package com.bd.deliverytiger.app.api.model.calculator


import com.google.gson.annotations.SerializedName

data class DeliveryType(
    @SerializedName("name")
    var name: String? = "",
    @SerializedName("daliveryRangeId")
    var daliveryRangeId: Int = 0,
    @SerializedName("onImage")
    var onImage: String? = "'",
    @SerializedName("offImage")
    var offImage: String? = ""
)