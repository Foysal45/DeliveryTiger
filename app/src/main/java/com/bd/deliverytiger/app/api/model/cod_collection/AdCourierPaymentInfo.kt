package com.bd.deliverytiger.app.api.model.cod_collection


import com.google.gson.annotations.SerializedName

data class AdCourierPaymentInfo(
    @SerializedName("paymentInProcessing")
    var paymentInProcessing: Double? = null,
    @SerializedName("paymentPaid")
    var paymentPaid: Double? = null,
    @SerializedName("paymentReady")
    var paymentReady: Double? = null
)