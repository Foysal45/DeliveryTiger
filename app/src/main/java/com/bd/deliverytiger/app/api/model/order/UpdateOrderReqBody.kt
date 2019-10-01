package com.bd.deliverytiger.app.api.model.order


import com.google.gson.annotations.SerializedName

data class UpdateOrderReqBody(
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
    @SerializedName("collectAddress")
    var collectAddress: String? = "",
    @SerializedName("collectionName")
    var collectionName: String? = ""
)