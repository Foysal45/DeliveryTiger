package com.bd.deliverytiger.app.api.model.status


import com.google.gson.annotations.SerializedName

data class StatusGroupModel(
    @SerializedName("statusGroupId")
    var statusGroupId: Int = 0,
    @SerializedName("reportStatusGroup")
    var reportStatusGroup: String = "",
    @SerializedName("fulfillmentStatusGroup")
    var fulfillmentStatusGroup: String = "",
    @SerializedName("orderTrackStatusGroup")
    var orderTrackStatusGroup: String = "",
    @SerializedName("orderTrackStatusPublicGroup")
    var orderTrackStatusPublicGroup: String = "",
    @SerializedName("dashboardStatusGroup")
    var dashboardStatusGroup: String = "",
    @SerializedName("dashboardSpanCount")
    var dashboardSpanCount: Int = 0,
    @SerializedName("dashboardViewColorType")
    var dashboardViewColorType: String = "",
    @SerializedName("dashboardViewOrderBy")
    var dashboardViewOrderBy: Int = 0,
    @SerializedName("dashboardRouteUrl")
    var dashboardRouteUrl: String = ""
)