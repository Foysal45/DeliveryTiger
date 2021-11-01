package com.bd.deliverytiger.app.api.model.payment_receieve


import com.google.gson.annotations.SerializedName

data class MerchantPayableReceiveableDetailRequest(
    @SerializedName("MerchantID")
    val merchantID: Int = 0,
    @SerializedName("FromFreeze")
    val fromFreeze: Int = 0
)