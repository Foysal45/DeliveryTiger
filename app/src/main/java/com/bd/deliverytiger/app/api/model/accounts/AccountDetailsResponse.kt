package com.bd.deliverytiger.app.api.model.accounts


import com.google.gson.annotations.SerializedName

data class AccountDetailsResponse(
    @SerializedName("NetAdjustedAmount")
    var netAdjustedAmount: Int = 0,
    @SerializedName("ReceivableOrderCount")
    var receivableOrderCount: Int = 0,
    @SerializedName("PayableOrderCount")
    var payableOrderCount: Int = 0,
    @SerializedName("TotalOrderCount")
    var totalOrderCount: Int = 0,
    @SerializedName("TotalMerchantReceivable")
    var totalMerchantReceivable: Int = 0,
    @SerializedName("TotalCodServiceCharge")
    var totalCodServiceCharge: Int = 0,
    @SerializedName("TotalCollectedAmount")
    var totalCollectedAmount: Int = 0,
    @SerializedName("TotalMerchantPayable")
    var totalMerchantPayable: Int = 0,

    @SerializedName("PayableOrders")
    var payableOrders: List<AccountsDetailsData> = listOf(),
    @SerializedName("ReceivableOrders")
    var receivableOrders: List<AccountsDetailsData> = listOf()
)