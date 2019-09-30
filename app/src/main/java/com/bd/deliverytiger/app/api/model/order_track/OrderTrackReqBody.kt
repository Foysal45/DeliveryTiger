package com.bd.deliverytiger.app.api.model.order_track


import com.google.gson.annotations.SerializedName

data class OrderTrackReqBody(
    @SerializedName("courierOrderId")
    var courierOrderId: String? = ""
)