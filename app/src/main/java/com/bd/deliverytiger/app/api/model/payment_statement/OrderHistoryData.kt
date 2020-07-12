package com.bd.deliverytiger.app.api.model.payment_statement


import com.google.gson.annotations.SerializedName

data class OrderHistoryData(
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
    var CODCharge: Int = 0,
    @SerializedName("DeliveryCharge")
    var deliveryCharge: Int = 0,
    @SerializedName("Amount")
    var amount: Int = 0,
    @SerializedName("CollectedAmount")
    var collectedAmount: Int = 0,
    @SerializedName("Type")
    var type: String? = "",
    @SerializedName("SDate")
    var sDate: String? = "",
    @SerializedName("OrderCode")
    var orderCode: String? = ""
)