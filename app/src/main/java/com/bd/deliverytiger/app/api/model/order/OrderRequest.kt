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
    var collectionAmount: Double,
    @SerializedName("deliveryCharge")
    var deliveryCharge: Double,
    @SerializedName("merchantId")
    var merchantId: Int,
    @SerializedName("breakableCharge")
    var breakableCharge: Double,
    @SerializedName("note")
    var note: String,
    @SerializedName("codCharge")
    var codCharge: Double,
    @SerializedName("collectionCharge")
    var collectionCharge: Double,
    @SerializedName("returnCharge")
    var returnCharge: Double,
    @SerializedName("packagingName")
    var packagingName: String,
    @SerializedName("packagingCharge")
    var packagingCharge: Double,
    @SerializedName("collectAddress")
    var collectAddress: String,
    @SerializedName("productType")
    var productType: String,
    @SerializedName("deliveryRangeId")
    var deliveryRangeId: Int,
    @SerializedName("weightRangeId")
    var weightRangeId: Int,
    @SerializedName("orderFrom")
    var orderFrom: String = "android",
    @SerializedName("isActive")
    var isActive: Boolean = true


)