package com.bd.deliverytiger.app.api.model.payment_receieve


import com.google.gson.annotations.SerializedName

data class MerchantPayableReceiveableDetailResponse(
    @SerializedName("AccountNo")
    val accountNo: String = "",
    @SerializedName("InstPayID")
    val instPayID: Int = 0,
    @SerializedName("Message")
    val message: Int = 0,
    @SerializedName("NetPayableAmount")
    val netPayableAmount: Int = 0,
    @SerializedName("PaymentType")
    val paymentType: Int = 0,
    @SerializedName("TransactionId")
    val transactionId: String = ""
)