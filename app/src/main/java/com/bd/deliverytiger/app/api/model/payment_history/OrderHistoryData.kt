package com.bd.deliverytiger.app.api.model.payment_history


import com.google.gson.annotations.SerializedName

data class OrderHistoryData(
    @SerializedName("Quantity")
    var quantity: String? = "0",
    @SerializedName("BookingCode")
    var bookingCode: String? = "0",
    @SerializedName("PaidAmount")
    var paidAmount: String? = "0",
    @SerializedName("Commission")
    var commission: String? = "0",
    @SerializedName("SalesPrice")
    var salesPrice: String? = "0",
    @SerializedName("UnitPrice")
    var unitPrice: String? = "0"
)