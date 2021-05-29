package com.bd.deliverytiger.app.api.model.live.order_management

import com.google.gson.annotations.SerializedName

data class OrderManagementRequestBody(
    @SerializedName("MerchantId")
    var merchantId: Int = 0,
    @SerializedName("DateFieldType")
    var dateFieldType: Int = 0,
    @SerializedName("FromDate")
    var fromDate: String? = "",
    @SerializedName("ToDate")
    var toDate: String? = "",
    @SerializedName("OrderStatus")
    var orderStatus: String? = "",
    @SerializedName("DealId")
    var dealId: String? = "",
    @SerializedName("BookingCode")
    var bookingCode: String? = "",
    @SerializedName("DealTitle")
    var dealTitle: String? = "",
    @SerializedName("Mobile")
    var mobile: String? = "",
    @SerializedName("DeliveryDistrict")
    var deliveryDistrict: String? = "",
    @SerializedName("BusinessModel")
    var businessModel: String? = "",
    @SerializedName("FilterType")
    var filterType: Int = 0,
    @SerializedName("Index")
    var index: Int = 0,
    @SerializedName("Count")
    var count: Int = 0
)