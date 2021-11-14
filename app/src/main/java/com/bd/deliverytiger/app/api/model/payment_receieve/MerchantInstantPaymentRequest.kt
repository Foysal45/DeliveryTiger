package com.bd.deliverytiger.app.api.model.payment_receieve


import com.google.gson.annotations.SerializedName

data class MerchantInstantPaymentRequest(
    @SerializedName("IPCharge")
    val iPCharge: Int = 0,
    @SerializedName("MerchantId")
    val merchantId: Int = 0,
    @SerializedName("NetPayableAmount")
    val netPayableAmount: Int = 0,
    @SerializedName("PaymentType")
    val paymentType: Int = 0,
    @SerializedName("PaymentMethod")
    val paymentMethod: Int = 0
)