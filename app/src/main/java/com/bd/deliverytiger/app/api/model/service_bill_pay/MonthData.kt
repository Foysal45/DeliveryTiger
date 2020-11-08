package com.bd.deliverytiger.app.api.model.service_bill_pay


import com.google.gson.annotations.SerializedName

data class MonthData(
    @SerializedName("MonthOrder")
    var monthOrder: Int = 0,
    @SerializedName("YearOrder")
    var yearOrder: Int = 0,
    @SerializedName("MonthName")
    var monthName: String? = "",
    @SerializedName("TotalAmount")
    var totalAmount: Int = 0,
    @SerializedName("OrderList")
    var orderList: List<OrderData> = listOf()

)