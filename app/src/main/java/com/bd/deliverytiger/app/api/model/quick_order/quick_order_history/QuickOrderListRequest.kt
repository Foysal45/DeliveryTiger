package com.bd.deliverytiger.app.api.model.quick_order.quick_order_history


import com.google.gson.annotations.SerializedName

data class QuickOrderListRequest(
    @SerializedName("fromDate")
    var fromDate: String? = "",
    @SerializedName("toDate")
    var toDate: String? = "",
    @SerializedName("merchantId")
    var merchantId: Int = 0
)