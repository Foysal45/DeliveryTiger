package com.bd.deliverytiger.app.api.model.dashboard


import com.google.gson.annotations.SerializedName

data class DashboardResponse(
    @SerializedName("pickDashboardViewModel")
    var pickDashboardViewModel: List<DashboardData>? = listOf(),
    @SerializedName("orderDashboardViewModel")
    var orderDashboardViewModel: List<DashboardData>? = listOf(),
    @SerializedName("paymentDashboardViewModel")
    var paymentDashboardViewModel: List<DashboardData>? = listOf()
)