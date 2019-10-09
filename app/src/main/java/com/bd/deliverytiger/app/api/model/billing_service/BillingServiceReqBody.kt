package com.bd.deliverytiger.app.api.model.billing_service


import com.google.gson.annotations.SerializedName

data class BillingServiceReqBody(
    @SerializedName("status")
    var status: Int? = 0,
    @SerializedName("statusList")
    var statusList: List<Int?>? = listOf(),
    @SerializedName("statusGroup")
    var statusGroup: MutableList<String> = mutableListOf(),
    @SerializedName("fromDate")
    var fromDate: String? = "",
    @SerializedName("toDate")
    var toDate: String? = "",
    @SerializedName("courierUserId")
    var courierUserId: Int? = 0,
    @SerializedName("podNumber")
    var podNumber: String? = "",
    @SerializedName("orderIds")
    var orderIds: String? = "",
    @SerializedName("index")
    var index: Int? = 0,
    @SerializedName("count")
    var count: Int? = 0
)