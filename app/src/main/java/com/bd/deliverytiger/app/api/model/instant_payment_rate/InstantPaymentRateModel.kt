package com.bd.deliverytiger.app.api.model.instant_payment_rate


import com.google.gson.annotations.SerializedName

data class InstantPaymentRateModel(
    @SerializedName("Charge")
    val charge: List<String> = listOf(),
    @SerializedName("Discount")
    val discount: List<String> = listOf()
)