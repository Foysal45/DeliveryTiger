package com.bd.deliverytiger.app.api.model.accounts


import com.google.gson.annotations.SerializedName

data class BankCheckForEftRequest(
    @SerializedName("BankName")
    val bankName: String = ""
)