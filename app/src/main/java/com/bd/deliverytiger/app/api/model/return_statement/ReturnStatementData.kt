package com.bd.deliverytiger.app.api.model.return_statement


import com.google.gson.annotations.SerializedName

data class ReturnStatementData(
    @SerializedName("date")
    var date: String? = "",
    @SerializedName("name")
    var name: String? = "",
    @SerializedName("order")
    var order: Int = 0,
    @SerializedName("orders")
    var orders: List<ReturnOrder> = listOf()
)