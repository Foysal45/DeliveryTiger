package com.bd.deliverytiger.app.api.model.service_bill_pay


import com.google.gson.annotations.SerializedName

data class OrderData(
    @SerializedName("TotalAmount")
    var totalAmount: Int = 0,
    @SerializedName("Name")
    var name: String? = "",
    @SerializedName("OrderCode")
    var orderCode: String? = ""
)