package com.bd.deliverytiger.app.api.model.instant_payment_update


import com.google.gson.annotations.SerializedName

data class InstantPaymentUpdateResponse(
    @SerializedName("courierUserId")
    var courierUserId: Int = 0,
    @SerializedName("userName")
    var userName: String? = "",
    @SerializedName("password")
    var password: String? = "",
    @SerializedName("mobile")
    var mobile: String? = "",
    @SerializedName("isActive")
    var isActive: Boolean = false,
    @SerializedName("address")
    var address: String? = "",
    @SerializedName("smsCharge")
    var smsCharge: Double = 0.0,
    @SerializedName("mailCharge")
    var mailCharge: Double = 0.0,
    @SerializedName("returnCharge")
    var returnCharge: Double = 0.0,
    @SerializedName("collectionCharge")
    var collectionCharge: Double = 0.0,
    @SerializedName("isSms")
    var isSms: Boolean = false,
    @SerializedName("isEmail")
    var isEmail: Boolean = false,
    @SerializedName("emailAddress")
    var emailAddress: String? = "",
    @SerializedName("bkashNumber")
    var bkashNumber: String? = "",
    @SerializedName("alterMobile")
    var alterMobile: String? = "",
    @SerializedName("sourceType")
    var sourceType: String? = "",
    @SerializedName("retentionUserId")
    var retentionUserId: Int = 0,
    @SerializedName("acquisitionUserId")
    var acquisitionUserId: Int = 0,
    @SerializedName("joinDate")
    var joinDate: String? = "",
    @SerializedName("isDocument")
    var isDocument: Boolean = false,
    @SerializedName("remarks")
    var remarks: String? = "",
    @SerializedName("companyName")
    var companyName: String? = "",
    @SerializedName("isCustomerSms")
    var isCustomerSms: Boolean = false,
    @SerializedName("isCustomerEmail")
    var isCustomerEmail: Boolean = false,
    @SerializedName("maxCodCharge")
    var maxCodCharge: Double = 0.0,
    @SerializedName("refreshtoken")
    var refreshtoken: String? = "",
    @SerializedName("isAutoProcess")
    var isAutoProcess: Boolean = false,
    @SerializedName("firebaseToken")
    var firebaseToken: String? = "",
    @SerializedName("credit")
    var credit: Double = 0.0,
    @SerializedName("fburl")
    var fburl: String? = "",
    @SerializedName("webURL")
    var webURL: String? = "",
    @SerializedName("isOfferActive")
    var isOfferActive: Boolean = false,
    @SerializedName("offerCodDiscount")
    var offerCodDiscount: Double = 0.0,
    @SerializedName("offerType")
    var offerType: Int = 0,
    @SerializedName("offerBkashDiscountDhaka")
    var offerBkashDiscountDhaka: Double = 0.0,
    @SerializedName("offerBkashDiscountOutSideDhaka")
    var offerBkashDiscountOutSideDhaka: Double = 0.0,
    @SerializedName("advancePayment")
    var advancePayment: Double = 0.0,
    @SerializedName("knowingSource")
    var knowingSource: String? = "",
    @SerializedName("priority")
    var priority: String? = "",
    @SerializedName("referrer")
    var referrer: String? = "",
    @SerializedName("referrerOrder")
    var referrerOrder: Int = 0,
    @SerializedName("referrerStartTime")
    var referrerStartTime: String? = "",
    @SerializedName("referrerEndTime")
    var referrerEndTime: String? = "",
    @SerializedName("orderType")
    var orderType: String? = "",
    @SerializedName("refereeOrder")
    var refereeOrder: Int = 0,
    @SerializedName("referrerIsActive")
    var referrerIsActive: Boolean = false,
    @SerializedName("refereeStartTime")
    var refereeStartTime: Any? = Any(),
    @SerializedName("refereeEndTime")
    var refereeEndTime: Any? = Any(),
    @SerializedName("preferredPaymentCycle")
    var preferredPaymentCycle: String? = "",
    @SerializedName("registrationFrom")
    var registrationFrom: String? = "",
    @SerializedName("isBlock")
    var isBlock: Boolean = false,
    @SerializedName("blockReason")
    var blockReason: String? = "",
    @SerializedName("weightRangeId")
    var weightRangeId: Int = 0,
    @SerializedName("deliveryRangeIdInside")
    var deliveryRangeIdInside: Int = 0,
    @SerializedName("deliveryRangeIdIOutside")
    var deliveryRangeIdIOutside: Int = 0,
    @SerializedName("districtId")
    var districtId: Int = 0,
    @SerializedName("thanaId")
    var thanaId: Int = 0,
    @SerializedName("preferredPaymentCycleDate")
    var preferredPaymentCycleDate: String? = ""
)