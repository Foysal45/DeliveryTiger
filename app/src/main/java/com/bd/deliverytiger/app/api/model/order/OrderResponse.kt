package com.bd.deliverytiger.app.api.model.order


import com.google.gson.annotations.SerializedName

data class OrderResponse(
    @SerializedName("id")
    var id: Int? = 0,
    @SerializedName("customerName")
    var customerName: String? = "",
    @SerializedName("mobile")
    var mobile: String? = "",
    @SerializedName("otherMobile")
    var otherMobile: String? = "",
    @SerializedName("address")
    var address: String? = "",
    @SerializedName("districtId")
    var districtId: Int? = 0,
    @SerializedName("thanaId")
    var thanaId: Int? = 0,
    @SerializedName("areaId")
    var areaId: Int? = 0,
    @SerializedName("paymentType")
    var paymentType: String? = "",
    @SerializedName("orderType")
    var orderType: String? = "",
    @SerializedName("weight")
    var weight: String? = "",
    @SerializedName("collectionName")
    var collectionName: String? = "",
    @SerializedName("collectionAmount")
    var collectionAmount: Int? = 0,
    @SerializedName("deliveryCharge")
    var deliveryCharge: Int? = 0,
    @SerializedName("isActive")
    var isActive: Boolean? = false,
    @SerializedName("updatedBy")
    var updatedBy: Int? = 0,
    @SerializedName("updatedOn")
    var updatedOn: String? = "",
    @SerializedName("confirmationDate")
    var confirmationDate: String? = "",
    @SerializedName("courierOrdersId")
    var courierOrdersId: String? = "",
    @SerializedName("orderDate")
    var orderDate: String? = "",
    @SerializedName("status")
    var status: Int? = 0,
    @SerializedName("postedOn")
    var postedOn: String? = "",
    @SerializedName("postedBy")
    var postedBy: Int? = 0,
    @SerializedName("merchantId")
    var merchantId: Int? = 0,
    @SerializedName("comment")
    var comment: String? = "",
    @SerializedName("podNumber")
    var podNumber: String? = "",
    @SerializedName("breakableCharge")
    var breakableCharge: Int? = 0,
    @SerializedName("note")
    var note: String? = "",
    @SerializedName("isConfirmedBy")
    var isConfirmedBy: String? = "",
    @SerializedName("codCharge")
    var codCharge: Int? = 0,
    @SerializedName("courierId")
    var courierId: Int? = 0,
    @SerializedName("collectionCharge")
    var collectionCharge: Int? = 0,
    @SerializedName("returnCharge")
    var returnCharge: Int? = 0,
    @SerializedName("packagingName")
    var packagingName: String? = "",
    @SerializedName("packagingCharge")
    var packagingCharge: Int? = 0,
    @SerializedName("collectAddress")
    var collectAddress: String? = "",
    @SerializedName("hubName")
    var hubName: String? = ""
)