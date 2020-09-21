package com.bd.deliverytiger.app.api.model.cod_collection


import com.google.gson.annotations.SerializedName

data class CourierOrderViewModel(
    @SerializedName("id")
    var id: Int? = null,
    @SerializedName("customerName")
    var customerName: String? = null,
    @SerializedName("courierOrdersId")
    var courierOrdersId: String? = null,
    @SerializedName("status")
    var status: String? = null,
    @SerializedName("statusType")
    var statusType: String? = null,
    @SerializedName("statusEng")
    var statusEng: String? = null,
    @SerializedName("statusId")
    var statusId: Int? = null,
    @SerializedName("logStatusId")
    var logStatusId: Int? = null,
    @SerializedName("logStatus")
    var logStatus: String? = null,
    @SerializedName("courierId")
    var courierId: Int? = null,
    @SerializedName("courierName")
    var courierName: String? = null,
    @SerializedName("comment")
    var comment: String? = null,
    @SerializedName("podNumber")
    var podNumber: String? = null,
    @SerializedName("hubName")
    var hubName: String? = null,
    @SerializedName("courierAddressContactInfo")
    var courierAddressContactInfo: CourierAddressContactInfo? = null,
    @SerializedName("courierOrderInfo")
    var courierOrderInfo: CourierOrderInfo? = null,
    @SerializedName("courierOrderDateDetails")
    var courierOrderDateDetails: CourierOrderDateDetails? = null,
    @SerializedName("actionUrl")
    var actionUrl: ActionUrl? = null,
    @SerializedName("userInfo")
    var userInfo: UserInfo? = null,
    @SerializedName("courierPrice")
    var courierPrice: CourierPrice? = null,
    @SerializedName("adCourierCommunicationInfo")
    var adCourierCommunicationInfo: AdCourierCommunicationInfo? = null,
    @SerializedName("classNameCss")
    var classNameCss: String? = null,
    @SerializedName("paidUnpaidColor")
    var paidUnpaidColor: String? = null,
    @SerializedName("buttonFlag")
    var buttonFlag: Boolean = false,
    @SerializedName("hubViewModel")
    var hubViewModel: HubInfo? = null
)