package com.bd.deliverytiger.app.api.model.live.order_management

import com.google.gson.annotations.SerializedName

data class OrderManagementResponseModel(
    @SerializedName("CouponId")
    var couponId: Int = 0,
    @SerializedName("ProfileId")
    var profileId: Int = 0,
    @SerializedName("IsAdvancePaymentHide")
    var isAdvancePaymentHide: Int = 0,
    @SerializedName("CustomerInfo")
    var customerInfo: OrderInfo? = OrderInfo(),
    @SerializedName("OrderInfo")
    var orderInfo: OrderInfo? = OrderInfo(),
    @SerializedName("PriceInfo")
    var priceInfo: OrderInfo? = OrderInfo(),
    @SerializedName("CourierInfo")
    var courierInfo: OrderInfo? = OrderInfo(),
    @SerializedName("CourierBarQRLink")
    var courierBarQRLink: String? = "",
    @SerializedName("ImageLink")
    var imageLink: String? = "",
    @SerializedName("MerchantStatusBng")
    var merchantStatusBng: String? = "",
    @SerializedName("IsConfirmedByMerchant")
    var isConfirmedByMerchant: Int = 0,
    @SerializedName("StatusType")
    var statusType: Int = 0,
    @SerializedName("StatusColor")
    var statusColor: String? = "",
    @SerializedName("IsDone_")
    var isDone: Int = 0,
    @SerializedName("DeliveryConditionMessage")
    var deliveryConditionMessage: String? = "",
    @SerializedName("BusinessModel")
    var businessModel: Int = 0,
    @SerializedName("CustomerDeliveryMobile")
    var customerDeliveryMobile: String? = "",
    @SerializedName("DeliveryAddress")
    var deliveryAddress: String? = "",
    @SerializedName("RemainingDate")
    var remainingDate: Int = 0,
    @SerializedName("ButtonLogo")
    var buttonLogo: String? = "",
    @SerializedName("Notes")
    var notes: String? = "",
    @SerializedName("ButtonList")
    var buttonList: List<ButtonData> = listOf(),
    @SerializedName("PaymentType")
    var paymentType: String? = "",
    @SerializedName("PaymentStatus")
    var paymentStatus: String? = "",
    @SerializedName("CashBackEventId")
    var cashBackEventId: Int = 0,
    @SerializedName("VoucherId")
    var voucherId: Int = 0,
    @SerializedName("EventId")
    var eventId: Int = 0,
    @SerializedName("EventName")
    var eventName: String? = "",
    @SerializedName("FolderName")
    var folderName: String? = "",
    @SerializedName("CommentedOn")
    var commentedOn: String? = "",
    @SerializedName("IsAcknowledge")
    var isAcknowledge: Int = 0,
    @SerializedName("DeliveryChargeAmountInSideDhaka")
    var deliveryChargeAmountInSideDhaka: Int = 0,
    @SerializedName("DeliveryChargeAmountOutSideDhaka")
    var deliveryChargeAmountOutSideDhaka: Int = 0,
    @SerializedName("DeliveryDist")
    var deliveryDist: Int = 0
)