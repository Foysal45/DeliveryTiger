package com.bd.deliverytiger.app.api.model.courier_info


import com.google.gson.annotations.SerializedName

data class CourierInfoModel(
    @SerializedName("courierUserId")
    var courierUserId: Int,
    @SerializedName("userName")
    var userName: String?,
    @SerializedName("password")
    var password: String?,
    @SerializedName("mobile")
    var mobile: String?,
    @SerializedName("isActive")
    var isActive: Boolean,
    @SerializedName("address")
    var address: String?,
    @SerializedName("smsCharge")
    var smsCharge: Double,
    @SerializedName("mailCharge")
    var mailCharge: Double,
    @SerializedName("returnCharge")
    var returnCharge: Double,
    @SerializedName("collectionCharge")
    var collectionCharge: Double,
    @SerializedName("isSms")
    var isSms: Boolean,
    @SerializedName("isEmail")
    var isEmail: Boolean,
    @SerializedName("emailAddress")
    var emailAddress: String?,
    @SerializedName("bkashNumber")
    var bkashNumber: String?,
    @SerializedName("alterMobile")
    var alterMobile: String?,
    @SerializedName("sourceType")
    var sourceType: String?,
    @SerializedName("retentionUserId")
    var retentionUserId: Int,
    @SerializedName("acquisitionUserId")
    var acquisitionUserId: Int,
    @SerializedName("joinDate")
    var joinDate: String?,
    @SerializedName("isDocument")
    var isDocument: Boolean,
    @SerializedName("remarks")
    var remarks: String?,
    @SerializedName("companyName")
    var companyName: String?,
    @SerializedName("isCustomerSms")
    var isCustomerSms: Boolean,
    @SerializedName("isCustomerEmail")
    var isCustomerEmail: Boolean,
    @SerializedName("maxCodCharge")
    var maxCodCharge: Double,
    @SerializedName("refreshtoken")
    var refreshtoken: String?,
    @SerializedName("isAutoProcess")
    var isAutoProcess: Boolean,
    @SerializedName("firebaseToken")
    var firebaseToken: String?,
    @SerializedName("credit")
    var credit: Double,
    @SerializedName("fburl")
    var fburl: String?,
    @SerializedName("webURL")
    var webURL: String?,
    @SerializedName("isOfferActive")
    var isOfferActive: Boolean,
    @SerializedName("offerCodDiscount")
    var offerCodDiscount: Double,
    @SerializedName("offerType")
    var offerType: Int,
    @SerializedName("offerBkashDiscountDhaka")
    var offerBkashDiscountDhaka: Double,
    @SerializedName("offerBkashDiscountOutSideDhaka")
    var offerBkashDiscountOutSideDhaka: Double,
    @SerializedName("districtId")
    var districtId: Int = 0,
    @SerializedName("thanaId")
    var thanaId: Int = 0,
    @SerializedName("preferredPaymentCycleDate")
    var preferredPaymentCycleDate: String? = "",
    @SerializedName("preferredPaymentCycle")
    var preferredPaymentCycle: String? = "",
    @SerializedName("adminUsers")
    var adminUsers: AdminUser? = AdminUser(),
    @SerializedName("isQuickOrderActive")
    var isQuickOrderActive: Boolean = false,
    @SerializedName("isBreakAble")
    var isBreakAble: Boolean = false,
    @SerializedName("isHeavyWeight")
    var isHeavyWeight: Boolean = false,
    @SerializedName("paymentServiceType")
    var paymentServiceType: Int = 0,
    @SerializedName("paymentServiceCharge")
    var paymentServiceCharge: Double = 0.0,
    @SerializedName("pohBalance")
    var pohBalance: Double = 0.0,
    @SerializedName("merchantAssignActive")
    var merchantAssignActive: Boolean = false,
    @SerializedName("customerSMSLimit")
    var customerSMSLimit: Int = 0,
    @SerializedName("isLoanActive")
    var isLoanActive: Boolean = false,
    @SerializedName("customerVoiceSmsLimit")
    var customerVoiceSmsLimit: Int = 0,

    //Internal
    var isOfferTaken: Boolean = false
)