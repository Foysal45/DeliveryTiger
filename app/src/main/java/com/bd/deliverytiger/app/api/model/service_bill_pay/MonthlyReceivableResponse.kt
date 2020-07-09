package com.bd.deliverytiger.app.api.model.service_bill_pay


import com.google.gson.annotations.SerializedName

data class MonthlyReceivableResponse(
    @SerializedName("TotalAmount")
    var totalAmount: Int = 0,
    @SerializedName("OrderList")
    var orderList: List<OrderData>? = listOf()
)