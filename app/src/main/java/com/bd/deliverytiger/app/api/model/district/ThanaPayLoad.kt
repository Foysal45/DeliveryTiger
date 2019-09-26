package com.bd.deliverytiger.app.api.model.district

import com.google.gson.annotations.SerializedName

class ThanaPayLoad {
    @SerializedName("thanaId")
    var thanaId = 0

    @SerializedName("thana")
    var thana: String? = null

    @SerializedName("deliveryCharge")
    var deliveryCharge = 0

    @SerializedName("thanaBng")
    var thanaBng: String? = null

    @SerializedName("isAdvPaymentActive")
    var isAdvPaymentActive = 0

    @SerializedName("appDeliveryCharge")
    var appDeliveryCharge = 0

    @SerializedName("appMultipleOrderDeliveryCharge")
    var appMultipleOrderDeliveryCharge = 0

    @SerializedName("appAdvPaymentDeliveryCharge")
    var appAdvPaymentDeliveryCharge = 0

    @SerializedName("parentId")
    var parentId = 0

    @SerializedName("isCity")
    var isCity = false

    @SerializedName("isCourier")
    var isCourier = 0

    @SerializedName("thanaPriority")
    var thanaPriority = 0

    @SerializedName("hasArea")
    var hasArea = 0

    @SerializedName("thirdPartyLocationId")
    var thirdPartyLocationId = 0

    @SerializedName("postalCode")
    var postalCode: String? = null

    @SerializedName("hasExpressDelivery")
    var hasExpressDelivery = 0

    @SerializedName("specialDeliverySpeed")
    var specialDeliverySpeed: String? = null

    @SerializedName("isPostalAddress")
    var isPostalAddress = 0

    @SerializedName("deliveryPaymentType")
    var deliveryPaymentType = 0



    override fun toString(): String {
        return "ThanaPayLoad{" +
                "ThanaId=" + thanaId.toString() +
                ", Thana='" + thana + '\''.toString() +
                ", DeliveryCharge=" + deliveryCharge.toString() +
                ", ThanaBng='" + thanaBng + '\''.toString() +
                ", IsAdvPaymentActive=" + isAdvPaymentActive.toString() +
                ", AppDeliveryCharge=" + appDeliveryCharge.toString() +
                ", AppMultipleOrderDeliveryCharge=" + appMultipleOrderDeliveryCharge.toString() +
                ", AppAdvPaymentDeliveryCharge=" + appAdvPaymentDeliveryCharge.toString() +
                ", ParentId=" + parentId.toString() +
                ", IsCity=" + isCity.toString() +
                ", IsCourier=" + isCourier.toString() +
                ", ThanaPriority=" + thanaPriority.toString() +
                ", HasArea=" + hasArea.toString() +
                ", ThirdPartyLocationId=" + thirdPartyLocationId.toString() +
                ", PostalCode='" + postalCode + '\''.toString() +
                ", HasExpressDelivery=" + hasExpressDelivery.toString() +
                ", SpecialDeliverySpeed='" + specialDeliverySpeed + '\''.toString() +
                ", IsPostalAddress=" + isPostalAddress.toString() +
                ", DeliveryPaymentType=" + deliveryPaymentType +
                '}'.toInt()
    }
}