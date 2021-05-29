package com.bd.deliverytiger.app.api.model.live.order_management

import com.google.gson.annotations.SerializedName

data class OrderManagementResponseBody (

    @SerializedName("CouponId")
    var couponId: Int = 0,
    @SerializedName("ProfileId")
    var profileId: Int = 0,
    @SerializedName("IsAdvancePaymentHide")
    var isAdvancePaymentHide: Int = 0,
    @SerializedName("PaymentInfoForCourierDelivery")
    var paymentInfoForCourierDelivery: Int = 0

    /*"CouponId": 4051300,
    "ProfileId": 1100,
    "IsAdvancePaymentHide": 0,
    "PaymentInfoForCourierDelivery": {
        "PaidUnpaidText": "Paid",
        "PaidImageLink": "https://s3.us-east-2.amazonaws.com/ajkerdeal-images-ohio-1/img/paid.png"*/

)