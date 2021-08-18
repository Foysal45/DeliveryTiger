package com.bd.deliverytiger.app.api.model.return_statement


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ReturnOrder(
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("customerName")
    var customerName: String? = "",
    @SerializedName("mobile")
    var mobile: String? = "",
    @SerializedName("otherMobile")
    var otherMobile: String? = "",
    @SerializedName("address")
    var address: String? = "",
    @SerializedName("districtId")
    var districtId: Int = 0,
    @SerializedName("thanaId")
    var thanaId: Int = 0,
    @SerializedName("areaId")
    var areaId: Int = 0,
    @SerializedName("paymentType")
    var paymentType: String? = "",
    @SerializedName("orderType")
    var orderType: String? = "",
    @SerializedName("weight")
    var weight: String? = "",
    @SerializedName("collectionName")
    var collectionName: String? = "",
    @SerializedName("collectionAmount")
    var collectionAmount: Double = 0.0,
    @SerializedName("deliveryCharge")
    var deliveryCharge: Double = 0.0,
    @SerializedName("isActive")
    var isActive: Boolean = false,
    @SerializedName("updatedBy")
    var updatedBy: Int = 0,
    @SerializedName("updatedOn")
    var updatedOn: String? = "",
    @SerializedName("confirmationDate")
    var confirmationDate: String? = "",
    @SerializedName("courierOrdersId")
    var courierOrdersId: String? = "",
    @SerializedName("orderDate")
    var orderDate: String? = "",
    @SerializedName("status")
    var status: Int = 0,
    @SerializedName("postedOn")
    var postedOn: String? = "",
    @SerializedName("postedBy")
    var postedBy: Int = 0,
    @SerializedName("merchantId")
    var merchantId: Int = 0,
    @SerializedName("comment")
    var comment: String? = "",
    @SerializedName("podNumber")
    var podNumber: String? = "",
    @SerializedName("breakableCharge")
    var breakableCharge: Double = 0.0,
    @SerializedName("thirdPartyCourierInfo")
    var thirdPartyCourierInfo: String? = "",
    @SerializedName("note")
    var note: String? = "",
    @SerializedName("isConfirmedBy")
    var isConfirmedBy: String? = "",
    @SerializedName("codCharge")
    var codCharge: Double = 0.0,
    @SerializedName("courierId")
    var courierId: Int = 0,
    @SerializedName("collectionCharge")
    var collectionCharge: Double = 0.0,
    @SerializedName("returnCharge")
    var returnCharge: Double = 0.0,
    @SerializedName("packagingName")
    var packagingName: String? = "",
    @SerializedName("packagingCharge")
    var packagingCharge: Double = 0.0,
    @SerializedName("collectAddress")
    var collectAddress: String? = "",
    @SerializedName("isDownloaded")
    var isDownloaded: Boolean = false,
    @SerializedName("hubName")
    var hubName: String? = "",
    @SerializedName("orderFrom")
    var orderFrom: String? = "",
    @SerializedName("courierCharge")
    var courierCharge: Double = 0.0,
    @SerializedName("isOpenBox")
    var isOpenBox: Boolean = false,
    @SerializedName("isAutoProcess")
    var isAutoProcess: Boolean = false,
    @SerializedName("isTakaCollectionFromCourier")
    var isTakaCollectionFromCourier: Boolean = false,
    @SerializedName("deliveryRangeId")
    var deliveryRangeId: Int = 0,
    @SerializedName("weightRangeId")
    var weightRangeId: Int = 0,
    @SerializedName("productType")
    var productType: String? = "",
    @SerializedName("collectAddressDistrictId")
    var collectAddressDistrictId: Int = 0,
    @SerializedName("collectAddressThanaId")
    var collectAddressThanaId: Int = 0,
    @SerializedName("deliveryUserId")
    var deliveryUserId: Int = 0,
    @SerializedName("riderAcceptDate")
    var riderAcceptDate: String? = "",
    @SerializedName("riderDeliveredDate")
    var riderDeliveredDate: String? = "",
    @SerializedName("merchantDeliveryDate")
    var merchantDeliveryDate: String? = "",
    @SerializedName("merchantCollectionDate")
    var merchantCollectionDate: String? = "",
    @SerializedName("officeDrop")
    var officeDrop: Boolean = false,
    @SerializedName("offerCode")
    var offerCode: String? = "",
    @SerializedName("offerCodDiscount")
    var offerCodDiscount: Double = 0.0,
    @SerializedName("offerBkashDiscount")
    var offerBkashDiscount: Double = 0.0,
    @SerializedName("isOfferCodActive")
    var isOfferCodActive: Boolean = false,
    @SerializedName("isOfferBkashActive")
    var isOfferBkashActive: Boolean = false,
    @SerializedName("classifiedId")
    var classifiedId: Int = 0,
    @SerializedName("actualPackagePrice")
    var actualPackagePrice: Double = 0.0,
    @SerializedName("collectionTimeSlotId")
    var collectionTimeSlotId: Int = 0,
    @SerializedName("collectionTime")
    var collectionTime: String? = "",
    @SerializedName("offerType")
    var offerType: String? = "",
    @SerializedName("relationType")
    var relationType: String? = "",
    @SerializedName("expectedDeliveryDate")
    var expectedDeliveryDate: String? = ""
): Parcelable