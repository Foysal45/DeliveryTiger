package com.bd.deliverytiger.app.api.model.billing_service


import com.google.gson.annotations.SerializedName

data class CourierOrderAmountDetail(
    @SerializedName("customerName")
    var customerName: String? = "",
    @SerializedName("courierOrdersId")
    var courierOrdersId: String? = "",
    @SerializedName("collectionAmount")
    var collectionAmount: Double = 0.0,
    @SerializedName("deliveryCharge")
    var deliveryCharge: Double = 0.0,
    @SerializedName("breakableCharge")
    var breakableCharge: Double = 0.0,
    @SerializedName("codCharge")
    var codCharge: Double = 0.0,
    @SerializedName("status")
    var status: String? = "",
    @SerializedName("serviceBillingStatus")
    var serviceBillingStatus: String? = "",
    @SerializedName("collectionCharge")
    var collectionCharge: Double = 0.0,
    @SerializedName("returnCharge")
    var returnCharge: Double = 0.0,
    @SerializedName("statusTextColorClass")
    var statusTextColorClass: String? = "",
    @SerializedName("totalAmount")
    var totalAmount: Double = 0.0,
    @SerializedName("className")
    var className: String? = "",
    @SerializedName("paidUnpaidColor")
    var paidUnpaidColor: String? = ""
)