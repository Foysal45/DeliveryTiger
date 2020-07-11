package com.bd.deliverytiger.app.api.model.payment_history


import com.google.gson.annotations.SerializedName

data class PaymentDetailsResponse(
    @SerializedName("NetPaidAmount")
    var netPaidAmount: Int = 0,
    @SerializedName("ModeOfPayment")
    var modeOfPayment: String? = "",
    @SerializedName("TransactionNo")
    var transactionNo: String? = "",
    @SerializedName("OrderList")
    var orderList: List<OrderHistoryData>? = listOf()
)