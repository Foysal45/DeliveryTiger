package com.bd.deliverytiger.app.api.model.location


import com.google.gson.annotations.SerializedName

data class AreaInfoModel(
    @SerializedName("AreaId")
    var areaId: Int = 0,
    @SerializedName("PostalCode")
    var postalCode: String = "",
    @SerializedName("IsPostalAddress")
    var isPostalAddress: Int = 0,
    @SerializedName("Area")
    var area: String = "",
    @SerializedName("DeliveryCharge")
    var deliveryCharge: Int = 0,
    @SerializedName("AreaBng")
    var areaBng: String = "",
    @SerializedName("IsAdvPaymentActive")
    var isAdvPaymentActive: Int = 0,
    @SerializedName("AppDeliveryCharge")
    var appDeliveryCharge: Int = 0,
    @SerializedName("AppMultipleOrderDeliveryCharge")
    var appMultipleOrderDeliveryCharge: Int = 0,
    @SerializedName("AppAdvPaymentDeliveryCharge")
    var appAdvPaymentDeliveryCharge: Int = 0,
    @SerializedName("ParentId")
    var parentId: Int = 0,
    @SerializedName("IsCity")
    var isCity: Boolean = false,
    @SerializedName("IsCourier")
    var isCourier: Int = 0,
    @SerializedName("AreaPriority")
    var areaPriority: Int = 0,
    @SerializedName("DistrictId")
    var districtId: Int = 0,
    @SerializedName("HasExpressDelivery")
    var hasExpressDelivery: Int = 0,
    @SerializedName("DeliveryPaymentType")
    var deliveryPaymentType: Int = 0,
    @SerializedName("IsBlocked")
    var isBlocked: Int = 0,
    @SerializedName("ParentName")
    var parentName: String = ""
)