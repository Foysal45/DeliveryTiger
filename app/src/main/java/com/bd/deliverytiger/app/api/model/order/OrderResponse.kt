package com.bd.deliverytiger.app.api.model.order


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OrderResponse(
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
    var collectionAmount: Float? = 0.0f,
    @SerializedName("deliveryCharge")
    var deliveryCharge: Float? = 0.0f,
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
    var breakableCharge: Float? = 0.0f,
    @SerializedName("note")
    var note: String? = "",
    @SerializedName("isConfirmedBy")
    var isConfirmedBy: String? = "",
    @SerializedName("codCharge")
    var codCharge: Float? = 0.0f,
    @SerializedName("courierId")
    var courierId: Int? = 0,
    @SerializedName("collectionCharge")
    var collectionCharge: Float? = 0.0f,
    @SerializedName("returnCharge")
    var returnCharge: Float? = 0.0f,
    @SerializedName("packagingName")
    var packagingName: String? = "",
    @SerializedName("packagingCharge")
    var packagingCharge: Float? = 0.0f,
    @SerializedName("collectAddress")
    var collectAddress: String? = "",
    @SerializedName("hubName")
    var hubName: String? = "",
    @SerializedName("productType")
    var productType: String = "",
    @SerializedName("deliveryRangeId")
    var deliveryRangeId: Int = 0,
    @SerializedName("weightRangeId")
    var weightRangeId: Int = 0,
    @SerializedName("isOpenBox")
    var isOpenBox: Boolean = false,


    @SerializedName("offerCode")
    var offerCode: String = "",
    @SerializedName("offerCodDiscount")
    var offerCodDiscount: Double = 0.0,
    @SerializedName("offerBkashDiscount")
    var offerBkashDiscount: Double = 0.0,
    @SerializedName("isOfferCodActive")
    var isOfferCodActive: Boolean = false,
    @SerializedName("isOfferBkashActive")
    var isOfferBkashActive: Boolean = false,
    @SerializedName("classifiedId")
    var classifiedId: Int = 0

) : Parcelable