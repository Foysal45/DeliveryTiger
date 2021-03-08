package com.bd.deliverytiger.app.api.model.location


import com.google.gson.annotations.SerializedName

data class ThanaInfoModel(
    @SerializedName("ThanaId")
    var thanaId: Int = 0,
    @SerializedName("Thana")
    var thana: String = "",
    @SerializedName("DeliveryCharge")
    var deliveryCharge: Int = 0,
    @SerializedName("ThanaBng")
    var thanaBng: String = "",
    @SerializedName("PostalCode")
    var postalCode: String = "",
    @SerializedName("IsPostalAddress")
    var isPostalAddress: Int = 0,
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
    @SerializedName("ThanaPriority")
    var thanaPriority: Int = 0,
    @SerializedName("HasArea")
    var hasArea: Int = 0,
    @SerializedName("ThirdPartyLocationId")
    var thirdPartyLocationId: Int = 0,
    @SerializedName("HasExpressDelivery")
    var hasExpressDelivery: Int = 0,
    @SerializedName("SpecialDeliverySpeed")
    var specialDeliverySpeed: String = "",
    @SerializedName("DeliveryPaymentType")
    var deliveryPaymentType: Int = 0,
    @SerializedName("IsBlock")
    var isBlock: Int = 0,
    @SerializedName("ParentName")
    var parentName: String = ""
)