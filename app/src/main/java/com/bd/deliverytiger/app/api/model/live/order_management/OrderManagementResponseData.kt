package com.bd.deliverytiger.app.api.model.live.order_management

import com.google.gson.annotations.SerializedName

data class OrderManagementResponseData (
    @SerializedName("DealsFeedResponseModel")
    var dealsFeedResponseModel: List<OrderManagementResponseModel> = listOf()
)