package com.bd.deliverytiger.app.api.model.bill_pay_history


import com.google.gson.annotations.SerializedName

data class BillPayHistoryResponse(
    @SerializedName("NetPaidAmount")
    var netPaidAmount: Int = 0,
    @SerializedName("ReferanceText")
    var referanceText: String? = "",
    @SerializedName("PaymentFrom")
    var paymentFrom: String? = "",
    @SerializedName("ModeOfPayment")
    var modeOfPayment: String? = "",
    @SerializedName("PaymentDate")
    var paymentDate: String? = "",
    @SerializedName("OrderList")
    var orderList: List<OrderAmount> = listOf()
)