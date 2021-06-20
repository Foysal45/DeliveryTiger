package com.bd.deliverytiger.app.api.model.order


import com.google.gson.annotations.SerializedName

data class UpdateOrderReqBody(
    @SerializedName("customerName")
    var customerName: String? = "",
    @SerializedName("mobile")
    var mobile: String? = "",
    @SerializedName("otherMobile")
    var otherMobile: String? = "",
    @SerializedName("address")
    var address: String? = "",
    @SerializedName("collectionName")
    var collectionName: String? = "",
    @SerializedName("collectionAmount")
    var collectionAmount: Double? = null,
    @SerializedName("codCharge")
    var codCharge: Double? = null,
    @SerializedName("officeDrop")
    var officeDrop: Boolean = false,
    @SerializedName("collectionCharge")
    var collectionCharge: Double? = null,
)