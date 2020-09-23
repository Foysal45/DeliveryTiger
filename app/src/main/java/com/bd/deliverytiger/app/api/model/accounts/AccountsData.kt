package com.bd.deliverytiger.app.api.model.accounts


import com.google.gson.annotations.SerializedName

data class AccountsData(
    @SerializedName("Name")
    var name: String?,
    @SerializedName("Count")
    var count: Int,
    @SerializedName("TotalAmount")
    var totalAmount: Double
)