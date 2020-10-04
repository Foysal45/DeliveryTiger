package com.bd.deliverytiger.app.api.model.billing_service


import com.google.gson.annotations.SerializedName

data class BillingServiceMainResponse(
    @SerializedName("totalData")
    var totalData: Int = 0,
    @SerializedName("totalAmount")
    var totalAmount: Double = 0.0,
    @SerializedName("totalAmountOnlyDelivery")
    var totalAmountOnlyDelivery: Double = 0.0,
    @SerializedName("totalAmountDeliveryTakaCollection")
    var totalAmountDeliveryTakaCollection: Double = 0.0,
    @SerializedName("totalDataCount")
    var totalDataCount: Int = 0,

    @SerializedName("courierOrderAmountDetails")
    var courierOrderAmountDetails: List<CourierOrderAmountDetail>
)