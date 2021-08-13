package com.bd.deliverytiger.app.api.model.deal_management


import com.google.gson.annotations.SerializedName

data class DealManagementResponse(
    @SerializedName("TotalCount")
    var totalCount: Int = 0,
    @SerializedName("data")
    var `data`: List<DealManagementData> = listOf()
)