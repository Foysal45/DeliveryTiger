package com.bd.deliverytiger.app.api.model.voucher


import com.google.gson.annotations.SerializedName

data class VoucherCheckRequest(
    @SerializedName("courierUserId")
    val courierUserId: Int = 0,
    @SerializedName("deliveryRangeId")
    val deliveryRangeId: Int = 0,
    @SerializedName("voucherCode")
    val voucherCode: String = ""
)