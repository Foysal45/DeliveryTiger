package com.bd.deliverytiger.app.api.model.instant_payment_update

import com.google.gson.annotations.SerializedName

data class UpdatePaymentCycleRequest(
        @SerializedName("CourierUserId")
        var courierUserId: Int = 0,
        @SerializedName("BkashNumber")
        var bkashNumber: String = "",
        @SerializedName("PreferredPaymentCycle")
        var preferredPaymentCycle: String = "",
)
