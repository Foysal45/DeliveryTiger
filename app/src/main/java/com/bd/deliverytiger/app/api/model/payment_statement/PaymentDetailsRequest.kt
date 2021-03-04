package com.bd.deliverytiger.app.api.model.payment_statement


import com.google.gson.annotations.SerializedName

data class PaymentDetailsRequest(
    @SerializedName("MerchantId")
    var merchantId: Int = 0,
    @SerializedName("TransactionNo")
    var transactionNo: String? = ""
)