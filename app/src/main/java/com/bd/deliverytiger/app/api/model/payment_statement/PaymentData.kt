package com.bd.deliverytiger.app.api.model.payment_statement


import com.google.gson.annotations.SerializedName

data class PaymentData(
    @SerializedName("NetPaidAmount")
    var netPaidAmount: Int = 0,
    @SerializedName("MerchantReceivable")
    var merchantReceivable: Int = 0,
    @SerializedName("MerchantPayable")
    var merchantPayable: Int = 0,
    @SerializedName("TotalTransactionOrderCount")
    var orderCount: Int = 0,
    @SerializedName("ModeOfPayment")
    var modeOfPayment: String? = "",
    @SerializedName("TransactionNo")
    var transactionNo: String? = "",
    @SerializedName("TransactionDate")
    var transactionDate: String? = ""
)