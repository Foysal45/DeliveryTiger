package com.bd.deliverytiger.app.api.model.accounts


import com.google.gson.annotations.SerializedName

data class BalanceInfo(
    @SerializedName("serviceCharge")
    var serviceCharge: Int,
    @SerializedName("credit")
    var credit: Int,
    @SerializedName("staticVal")
    var staticVal: Int,
    @SerializedName("calculatedCollectionAmount")
    var calculatedCollectionAmount: Int,

    @SerializedName("adjustBalance")
    var adjustBalance: Int
)