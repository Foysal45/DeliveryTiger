package com.bd.deliverytiger.app.api.model.dashboard


import com.google.gson.annotations.SerializedName

data class DashboardData(
    @SerializedName("statusGroupId")
    var statusGroupId: Int = 0,
    @SerializedName("name")
    var name: String? = "",
    @SerializedName("count")
    var count: Int = 0,
    @SerializedName("dashboardSpanCount")
    var dashboardSpanCount: Int? = 0,
    @SerializedName("dashboardViewColorType")
    var dashboardViewColorType: String? = "",
    @SerializedName("dashboardViewOrderBy")
    var dashboardViewOrderBy: Int? = 0,
    @SerializedName("dashboardRouteUrl")
    var dashboardRouteUrl: String? = "",
    @SerializedName("dashboardCountSumView")
    var dashboardCountSumView: String? = "",
    @SerializedName("totalAmount")
    var totalAmount: Double = 0.0,
    @SerializedName("dashboardStatusFilter")
    var dashboardStatusFilter: String = "",
    @SerializedName("dashboardImageUrl")
    var dashboardImageUrl: String = "",

    var viewType: Int = 0,
    var paymentDate: String = "",
    var availability: Boolean = false,
    var availabilityMessage: String = "",
    var paymentProcessingTime: String = "12-24",

    var currentRequestDate: String = "",
    var currentPaymentAmount: Int = 0,
    var currentPaymentStatus: Int = 0,
    var currentPaymentType: Int = 0,
    var currentPaymentMethod: Int = 0,
    var isPaymentProcessing: Int = 0

)