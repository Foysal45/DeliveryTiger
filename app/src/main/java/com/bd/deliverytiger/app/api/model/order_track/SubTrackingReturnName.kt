package com.bd.deliverytiger.app.api.model.order_track

import com.google.gson.annotations.SerializedName

data class SubTrackingReturnName(
    @SerializedName("name")
    var name: String? = "",
    @SerializedName("value")
    var value: String? = "",
    @SerializedName("hubAddress")
    var hubAddress: String? = "",
    @SerializedName("longitude")
    var longitude: String? = "",
    @SerializedName("latitude")
    var latitude: String? = "",
    @SerializedName("hubMobile")
    var hubMobile: String? = ""
)