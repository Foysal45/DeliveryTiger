package com.bd.deliverytiger.app.api.model.payment_receieve


import com.google.gson.annotations.SerializedName

data class MerchantPayableReceivableDetailResponse(
    @SerializedName("InstantPaymentCharge")
    val instantPaymentCharge: Int = 0,
    @SerializedName("NetPayableAmount")
    val netPayableAmount: Int = 0,
    @SerializedName("PayableAmount")
    val payableAmount: Int = 0
)