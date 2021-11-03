package com.bd.deliverytiger.app.api.model.voucher


import com.google.gson.annotations.SerializedName

data class VoucherCheckResponse(
    @SerializedName("applicableQuantity")
    val applicableQuantity: Int = 0,
    @SerializedName("courierUserId")
    val courierUserId: Int = 0,
    @SerializedName("deliveryRangeId")
    val deliveryRangeId: Int = 0,
    @SerializedName("endTime")
    val endTime: String = "",
    @SerializedName("isActive")
    val isActive: Boolean = false,
    @SerializedName("merchantMobile")
    val merchantMobile: String = "",
    @SerializedName("message")
    val message: String = "",
    @SerializedName("startTime")
    val startTime: String = "",
    @SerializedName("voucherCode")
    val voucherCode: String = "",
    @SerializedName("voucherDiscount")
    val voucherDiscount: Double = 0.0,
    @SerializedName("voucherId")
    val voucherId: Int = 0
)