package com.bd.deliverytiger.app.api.model.live.order_management

import com.google.gson.annotations.SerializedName

data class OrderInfo(
    @SerializedName("DealTitle")
    var dealTitle: String? = "",
    @SerializedName("BookingDateDesktopFormat")
    var bookingDateDesktopFormat: String? = "",
    @SerializedName("BookingDate")
    var bookingDate: String? = "",
    @SerializedName("CouponQtn")
    var couponQtn: Int = 0,
    @SerializedName("DeliveryCharge")
    var deliveryCharge: String? = "",
    @SerializedName("IsDone")
    var isDone: Int = 0,
    @SerializedName("Sizes")
    var sizes: String? = "",
    @SerializedName("Colors")
    var colors: String? = "",
    @SerializedName("ProductDeliveryDate")
    var productDeliveryDate: String? = "",
    @SerializedName("PODNumber")
    var pODNumber: String? = "",
    @SerializedName("DealId")
    var dealId: Int = 0,
    @SerializedName("OrderNote")
    var orderNote: String? = "",
    @SerializedName("IsEventOrder")
    var isEventOrder: String? = "",
    @SerializedName("IsGroupBuyOrder")
    var isGroupBuyOrder: String? = "",
    @SerializedName("CardType")
    var cardType: String? = "",
    @SerializedName("DeliveryDate")
    var deliveryDate: String? = "",
    @SerializedName("CrmConfirmationDate")
    var crmConfirmationDate: String? = "",
    @SerializedName("CrmConfirmationDateToShow")
    var crmConfirmationDateToShow: String? = "",
    @SerializedName("Remarks")
    var remarks: String? = "",
    @SerializedName("CrmConfirmationDateForPanel")
    var crmConfirmationDateForPanel: String? = ""
)