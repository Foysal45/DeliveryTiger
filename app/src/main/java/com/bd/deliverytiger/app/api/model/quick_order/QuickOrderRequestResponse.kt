package com.bd.deliverytiger.app.api.model.quick_order


import com.google.gson.annotations.SerializedName

data class QuickOrderRequestResponse(
    @SerializedName("orderRequestId")
    var orderRequestId: Int = 0,
    @SerializedName("courierUserId")
    var courierUserId: Int = 0,
    @SerializedName("requestOrderAmount")
    var requestOrderAmount: Int = 0,
    @SerializedName("requestDate")
    var requestDate: String? = ""
)