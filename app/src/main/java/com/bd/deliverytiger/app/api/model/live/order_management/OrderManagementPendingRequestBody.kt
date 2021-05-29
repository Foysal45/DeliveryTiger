package com.bd.deliverytiger.app.api.model.live.order_management

import com.google.gson.annotations.SerializedName

data class OrderManagementPendingRequestBody(
    @SerializedName("ChannelId")
    var channelId: Int = 0,
    @SerializedName("OrderId")
    var orderId: Int = 0,
    @SerializedName("ProductId")
    var productId: Int = 0,
    @SerializedName("PhoneNumber")
    var phoneNumber: String? = "",
    @SerializedName("ProductName")
    var productName: String? = "",
    @SerializedName("Index")
    var index: Int = 0,
    @SerializedName("Count")
    var count: Int = 0,
    @SerializedName("ChannelType")
    var channelType: String = "merchant"
)