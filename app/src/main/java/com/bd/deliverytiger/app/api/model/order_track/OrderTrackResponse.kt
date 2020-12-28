package com.bd.deliverytiger.app.api.model.order_track

import com.google.gson.annotations.SerializedName

data class OrderTrackResponse(
    @SerializedName("courierOrdersViewModel")
    var courierOrdersViewModel: CourierOrdersViewModel = CourierOrdersViewModel(),
    @SerializedName("orderTrackingGroupViewModel")
    var orderTrackingGroupViewModel: List<OrderTrackData> = listOf()
)
