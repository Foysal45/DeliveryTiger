package com.bd.deliverytiger.app.api.model.order_track


import com.google.gson.annotations.SerializedName

data class SubTrackingName(
    @SerializedName("name")
    var name: String? = "",
    @SerializedName("latitude")
    var latitude: String? = "",
    @SerializedName("longitude")
    var longitude: String? = ""
)