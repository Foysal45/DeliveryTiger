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
    @SerializedName("isOpenBox")
    var isOpenBox: Boolean,
    @SerializedName("orderFrom")
    var orderFrom: String = "android",
    @SerializedName("version")
    var version: String = "",
    @SerializedName("isActive")
    var isActive: Boolean = true,

    @SerializedName("collectAddressDistrictId")
    var collectAddressDistrictId: Int = 0,
    @SerializedName("collectAddressThanaId")
    var collectAddressThanaId: Int = 0,
    @SerializedName("merchantDeliveryDate")
    var merchantDeliveryDate: String = "",
    @SerializedName("merchantCollectionDate")
    var merchantCollectionDate: String = "",
    @SerializedName("officeDrop")
    var officeDrop: Boolean = false,
    @SerializedName("actualPackagePrice")
    var actualPackagePrice: Double,
    @SerializedName("collectionTimeSlotId")
    var collectionTimeSlotId: Int,
    @SerializedName("collectionTime")
    var collectionTime: String = "",
    @SerializedName("offerType")
    var offerType: String = "",
    @SerializedName("relationType")
    var relationType: String = "",
    @SerializedName("serviceType")
    var serviceType: String = "",
    @SerializedName("isHeavyWeight")
    var isHeavyWeight: Boolean = false,
    @SerializedName("voucherDiscount")
    var voucherDiscount: Int = 0,
    @SerializedName("voucherCode")
    var voucherCode: String = "",
    @SerializedName("voucherDeliveryRangeId")
    var voucherDeliveryRangeId: Int = 0
)