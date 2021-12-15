package com.bd.deliverytiger.app.api.model.accounts


import com.google.gson.annotations.SerializedName

data class BankCheckForEftResponse(
    @SerializedName("Bank")
    var bank: List<Bank> = listOf(),
    @SerializedName("IsMatch")
    var isMatch: Int = 0,
    @SerializedName("Message")
    var message: String = ""
)