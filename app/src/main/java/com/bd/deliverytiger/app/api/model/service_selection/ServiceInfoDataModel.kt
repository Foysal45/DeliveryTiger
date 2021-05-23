package com.bd.deliverytiger.app.api.model.service_selection


import com.google.gson.annotations.SerializedName

data class ServiceInfoDataModel(
    @SerializedName("serviceTypeName")
    var serviceTypeName: String? = "",
    @SerializedName("serviceInfo")
    var serviceInfo: String? = "",
    @SerializedName("deliveryRangeId")
    var deliveryRangeId: List<Int>? = listOf()
)