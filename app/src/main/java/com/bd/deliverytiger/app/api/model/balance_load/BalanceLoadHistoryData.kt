package com.bd.deliverytiger.app.api.model.balance_load


import com.google.gson.annotations.SerializedName

data class BalanceLoadHistoryData(
    @SerializedName("AdvanceAmount")
    var advanceAmount: Int = 0,
    @SerializedName("MerchantId")
    var merchantId: Int = 0,
    @SerializedName("TransactionId")
    var transactionId: String? = "",
    @SerializedName("AdvanceDate")
    var advanceDate: String? = "",
    @SerializedName("CompanyName")
    var companyName: String? = ""
)