package com.bd.deliverytiger.app.api.model.instant_payment_status


import com.google.gson.annotations.SerializedName

data class InstantPaymentActivationStatusResponse(
    @SerializedName("HasInstantPayment")
    var hasInstantPayment: Int = 0
)