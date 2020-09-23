package com.bd.deliverytiger.app.api.model.rider


import com.google.gson.annotations.SerializedName

data class RiderInfo(
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("name")
    var name: String? = "",
    @SerializedName("mobile")
    var mobile: String? = "",
    @SerializedName("latitude")
    var latitude: String? = "0.0",
    @SerializedName("longitude")
    var longitude: String? = "0.0",
    @SerializedName("courierOrderIds")
    var courierOrderIds: List<String>? = listOf()
)