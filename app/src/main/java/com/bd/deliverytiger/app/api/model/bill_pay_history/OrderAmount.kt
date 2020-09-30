package com.bd.deliverytiger.app.api.model.bill_pay_history


import com.google.gson.annotations.SerializedName

data class OrderAmount(
    @SerializedName("TotalAmount")
    var totalAmount: Int = 0,
    @SerializedName("OrderCode")
    var orderCode: String? = ""
)