package com.bd.deliverytiger.app.api.model.accounts


import com.google.gson.annotations.SerializedName

data class BalanceInfo(
    @SerializedName("serviceCharge")
    var serviceCharge: Int,
    @SerializedName("adjustBalance")
    var adjustBalance: Int
)