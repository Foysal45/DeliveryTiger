package com.bd.deliverytiger.app.api.model.order_track


import com.google.gson.annotations.SerializedName

data class OrderTrackData(
    @SerializedName("trackingName")
    var trackingName: String? = "",
    @SerializedName("trackingColor")
    var trackingColor: String? = "",
    @SerializedName("trackingDate")
    var trackingDate: String? = "",
    @SerializedName("subTrackingName")
    var subTrackingName: SubTrackingName? = SubTrackingName()
)