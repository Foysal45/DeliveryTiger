package com.bd.deliverytiger.app.api.model.location


import com.google.gson.annotations.SerializedName

data class DistrictInfoModel(
    @SerializedName("ThanaHome")
    var thanaHome: List<ThanaInfoModel> = listOf(),
    @SerializedName("DistrictPriority")
    var districtPriority: Int = 0,
    @SerializedName("ParentId")
    var parentId: Int = 0,
    @SerializedName("IsCity")
    var isCity: Boolean = false,
    @SerializedName("IsCourier")
    var isCourier: Int = 0,
    @SerializedName("ThirdPartyLocationId")
    var thirdPartyLocationId: Int = 0,
    @SerializedName("DeliveryPaymentType")
    var deliveryPaymentType: Int = 0,
    @SerializedName("DistrictId")
    var districtId: Int = 0,
    @SerializedName("District")
    var district: String = "",
    @SerializedName("DeliveryCharge")
    var deliveryCharge: Int = 0,
    @SerializedName("DistrictBng")
    var districtBng: String = "",
    @SerializedName("IsAdvPaymentActive")
    var isAdvPaymentActive: Int = 0,
    @SerializedName("AppDeliveryCharge")
    var appDeliveryCharge: Int = 0,
    @SerializedName("AppMultipleOrderDeliveryCharge")
    var appMultipleOrderDeliveryCharge: Int = 0,
    @SerializedName("AppAdvPaymentDeliveryCharge")
    var appAdvPaymentDeliveryCharge: Int = 0,
    @SerializedName("DistrictOptions")
    var districtOptions: String = "0",
    @SerializedName("DistrictOptionsMsg")
    var districtOptionsMsg: String = ""
)