package com.bd.deliverytiger.app.api.model.charge


import com.google.gson.annotations.SerializedName

data class WeightRangeWiseData(
    @SerializedName("deliveryRangeId")
    var deliveryRangeId: Int = 0,
    @SerializedName("deliveryType")
    var deliveryType: String = "",
    @SerializedName("weightRangeId")
    var weightRangeId: Int = 0,
    @SerializedName("chargeAmount")
    var chargeAmount: Double = 0.0,
    @SerializedName("days")
    var days: String = "",
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
    @SerializedName("cityDeliveryCharge")
    var cityDeliveryCharge: Double = 0.0,
    @SerializedName("deliveryCharge")
    var deliveryCharge: Double = 0.0,
    @SerializedName("extraDeliveryCharge")
    var extraDeliveryCharge: Double = 0.0,
    @SerializedName("type")
    var type: String? = ""
)