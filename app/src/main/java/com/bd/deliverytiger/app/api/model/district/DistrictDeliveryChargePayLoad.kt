package com.bd.deliverytiger.app.api.model.district

import com.google.gson.annotations.SerializedName

class DistrictDeliveryChargePayLoad {
    @SerializedName("parentId")
    var parentId: Int = 0

    @SerializedName("isCity")
    var isCity: Boolean = false

    @SerializedName("isCourier")
    var isCourier: Int = 0

    @SerializedName("districtId")
    var districtId: Int = 0

    @SerializedName("district")
    var district: String? = null

    @SerializedName("deliveryCharge")
    var deliveryCharge: Int = 0

    @SerializedName("districtBng")
    var districtBng: String? = null

    @SerializedName("isAdvPaymentActive")
    var isAdvPaymentActive: Int = 0

    @SerializedName("appDeliveryCharge")
    var appDeliveryCharge: Int = 0

    @SerializedName("appMultipleOrderDeliveryCharge")
    var appMultipleOrderDeliveryCharge: Int = 0

    @SerializedName("appAdvPaymentDeliveryCharge")
    var appAdvPaymentDeliveryCharge: Int = 0

    @SerializedName("districtPriority")
    var districtPriority: Int = 0

    @SerializedName("thirdPartyLocationId")
    var thirdPartyLocationId: Int = 0

    @SerializedName("thanaHome")
    var thanaHome: List<ThanaPayLoad>? =
        null




    override fun toString(): String {
        return "DistrictDeliveryChargePayLoad{" +
                "ParentId=" + parentId.toString() +
                ", IsCity=" + isCity.toString() +
                ", IsCourier=" + isCourier.toString() +
                ", DistrictId=" + districtId.toString() +
                ", District='" + district + '\''.toString() +
                ", DeliveryCharge=" + deliveryCharge.toString() +
                ", DistrictBng='" + districtBng + '\''.toString() +
                ", IsAdvPaymentActive=" + isAdvPaymentActive.toString() +
                ", AppDeliveryCharge=" + appDeliveryCharge.toString() +
                ", AppMultipleOrderDeliveryCharge=" + appMultipleOrderDeliveryCharge.toString() +
                ", AppAdvPaymentDeliveryCharge=" + appAdvPaymentDeliveryCharge.toString() +
                ", DistrictPriority=" + districtPriority.toString() +
                ", ThanaHome=" + thanaHome.toString() +
                ", ThirdPartyLocationId=" + thirdPartyLocationId +
                '}'.toInt()
    }
}