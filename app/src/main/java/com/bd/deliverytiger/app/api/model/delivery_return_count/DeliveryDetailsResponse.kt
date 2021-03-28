package com.bd.deliverytiger.app.api.model.delivery_return_count


import com.google.gson.annotations.SerializedName

data class DeliveryDetailsResponse(
    @SerializedName("courierOrdersId")
    var courierOrdersId: String? = "",
    @SerializedName("orderDate")
    var orderDate: String? = "",
    @SerializedName("customerName")
    var customerName: String? = "",
    @SerializedName("mobile")
    var mobile: String? = "",
    @SerializedName("statusNameEng")
    var statusNameEng: String? = "",
    @SerializedName("merchantId")
    var merchantId: Int = 0,
    @SerializedName("status")
    var status: Int = 0,
    @SerializedName("collectionName")
    var collectionName: String? = ""
)