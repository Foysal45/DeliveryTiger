package com.bd.deliverytiger.app.api.model.balance_load


import com.google.gson.annotations.SerializedName

data class BalanceLimitResponse(
    @SerializedName("minAmount")
    var minAmount: Int = 0,
    @SerializedName("maxAmount")
    var maxAmount: Int = 0
)