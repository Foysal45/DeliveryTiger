package com.bd.deliverytiger.app.api.model.order


import com.google.gson.annotations.SerializedName

data class OrderRequest(

    @SerializedName("customerName")
    var customerName: String,
    @SerializedName("mobile")
    var mobile: String,
    @SerializedName("otherMobile")
    var otherMobile: String,
    @SerializedName("address")
    var address: String,
    @SerializedName("districtId")
    var districtId: Int,
    @SerializedName("thanaId")
    var thanaId: Int,
    @SerializedName("areaId")
    var areaId: Int,
    @SerializedName("paymentType")
    var paymentType: String,
    @SerializedName("orderType")
    var orderType: String,
    @SerializedName("weight")
    var weight: String,
    @SerializedName("collectionName")
    var collectionName: String,
    @SerializedName("collectionAmount")
    var collectionAmount: Int,
    @SerializedName("deliveryCharge")
    var deliveryCharge: Int,
    @SerializedName("merchantId")
    var merchantId: Int,
    @SerializedName("breakableCharge")
    var breakableCharge: Int,
    @SerializedName("note")
    var note: String,
    @SerializedName("codCharge")
    var codCharge: Int,
    @SerializedName("collectionCharge")
    var collectionCharge: Int,
    @SerializedName("returnCharge")
    var returnCharge: Int,
    @SerializedName("packagingName")
    var packagingName: String,
    @SerializedName("packagingCharge")
    var packagingCharge: Int,
    @SerializedName("collectAddress")
    var collectAddress: String,
    @SerializedName("isActive")
    var isActive: Boolean = true,
    @SerializedName("orderFrom")
    var orderFrom: String = "android",
    //
    @SerializedName("updatedBy")
    var updatedBy: Int = 0,
    @SerializedName("updatedOn")
    var updatedOn: String = "",
    @SerializedName("confirmationDate")
    var confirmationDate: String = "",
    @SerializedName("courierOrdersId")
    var courierOrdersId: String = "",
    @SerializedName("orderDate")
    var orderDate: String = "",
    @SerializedName("status")
    var status: Int = 0,
    @SerializedName("postedOn")
    var postedOn: String = "",
    @SerializedName("postedBy")
    var postedBy: Int = 0,
    @SerializedName("comment")
    var comment: String = "",
    @SerializedName("podNumber")
    var podNumber: String = "",
    @SerializedName("isConfirmedBy")
    var isConfirmedBy: String = "",
    @SerializedName("courierId")
    var courierId: Int = 0,
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("hubName")
    var hubName: String = ""
)