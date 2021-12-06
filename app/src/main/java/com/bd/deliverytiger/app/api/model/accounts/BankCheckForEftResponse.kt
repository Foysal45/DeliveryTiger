package com.bd.deliverytiger.app.api.model.accounts


import com.google.gson.annotations.SerializedName

data class BankCheckForEftResponse(
    @SerializedName("IsMatch")
    val isMatch: Int = 0,
    @SerializedName("Message")
    val message: String = ""
)