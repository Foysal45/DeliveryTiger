package com.bd.deliverytiger.app.api.model.accounts


import com.google.gson.annotations.SerializedName

data class Bank(
    @SerializedName("BankName")
    var bankName: String = ""
)