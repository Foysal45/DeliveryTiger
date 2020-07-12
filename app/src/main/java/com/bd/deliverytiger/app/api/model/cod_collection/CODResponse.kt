package com.bd.deliverytiger.app.api.model.cod_collection


import com.google.gson.annotations.SerializedName

data class CODResponse(
    @SerializedName("totalCount")
    var totalCount: Double? = null,
    @SerializedName("adTotalCollectionAmount")
    var adTotalCollectionAmount: Double? = null,
    @SerializedName("adCourierPaymentInfo")
    var adCourierPaymentInfo: AdCourierPaymentInfo? = null,
    @SerializedName("courierOrderViewModel")
    var courierOrderViewModel: List<CourierOrderViewModel>? = null
)