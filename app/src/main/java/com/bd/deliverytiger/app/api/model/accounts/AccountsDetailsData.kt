package com.bd.deliverytiger.app.api.model.accounts


import com.google.gson.annotations.SerializedName

data class AccountsDetailsData(
    @SerializedName("TotalCharge")
    var totalCharge: Int = 0,
    @SerializedName("CollectionCharge")
    var collectionCharge: Int = 0,
    @SerializedName("PackagingCharge")
    var packagingCharge: Int = 0,
    @SerializedName("ReturnCharge")
    var returnCharge: Int = 0,
    @SerializedName("BreakableCharge")
    var breakableCharge: Int = 0,
    @SerializedName("CODCharge")
    var cODCharge: Int = 0,
    @SerializedName("DeliveryCharge")
    var deliveryCharge: Int = 0,
    @SerializedName("CollectedAmount")
    var collectedAmount: Int = 0,
    @SerializedName("AdjustedAmount")
    var adjustedAmount: Int = 0,
    @SerializedName("AccReceiveable")
    var accReceiveable: Int = 0,
    @SerializedName("MerchantPayable")
    var merchantPayable: Int = 0,
    @SerializedName("OrderType")
    var orderType: String? = "",
    @SerializedName("FreezeDate")
    var freezeDate: String? = "",
    @SerializedName("OrderCode")
    var orderCode: String? = ""
)