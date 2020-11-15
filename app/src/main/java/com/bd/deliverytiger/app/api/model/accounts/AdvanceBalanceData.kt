package com.bd.deliverytiger.app.api.model.accounts


import com.google.gson.annotations.SerializedName

data class AdvanceBalanceData(
    @SerializedName("MerchantId")
    var merchantId: Int = 0,
    @SerializedName("AccReceivable")
    var accReceivable: Int = 0,
    @SerializedName("Balance")
    var balance: Int = 0,
    @SerializedName("AmountAdjusted")
    var amountAdjusted: Int = 0,
    @SerializedName("AdvanceAmount")
    var advanceAmount: Int = 0,
    @SerializedName("MerchantName")
    var merchantName: String? = ""
)