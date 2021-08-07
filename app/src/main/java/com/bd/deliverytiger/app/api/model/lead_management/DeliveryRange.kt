package com.bd.deliverytiger.app.api.model.lead_management


import com.google.gson.annotations.SerializedName

data class DeliveryRange(
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("name")
    var name: String? = "",
    @SerializedName("day")
    var day: String? = "",
    @SerializedName("isActive")
    var isActive: Boolean = false,
    @SerializedName("ranking")
    var ranking: Int = 0,
    @SerializedName("dayType")
    var dayType: String? = "",
    @SerializedName("onImageLink")
    var onImageLink: String? = "",
    @SerializedName("offImageLink")
    var offImageLink: String? = "",
    @SerializedName("showHide")
    var showHide: Int = 0,
    @SerializedName("deliveryAlertMessage")
    var deliveryAlertMessage: String? = "",
    @SerializedName("loginHours")
    var loginHours: String? = "",
    @SerializedName("dateAdvance")
    var dateAdvance: String? = "",
    @SerializedName("courierDeliveryCharge")
    var courierDeliveryCharge: Double = 0.0,
    @SerializedName("type")
    var type: String? = "",
    @SerializedName("priorityService")
    var priorityService: Int = 0,
    @SerializedName("addCddDate")
    var addCddDate: Int = 0
)