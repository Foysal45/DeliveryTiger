package com.bd.deliverytiger.app.api.model.service_bill_pay


import com.google.gson.annotations.SerializedName

data class OrderData(
    @SerializedName("IsCashCollected")
    var isCashCollected: Int = 0,
    @SerializedName("TotalAmount")
    var totalAmount: Int = 0,
    @SerializedName("Name")
    var name: String? = "",
    @SerializedName("MonthName")
    var monthName: String? = "",
    @SerializedName("OrderCode")
    var orderCode: String? = ""
)