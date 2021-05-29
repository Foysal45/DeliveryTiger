package com.bd.deliverytiger.app.api.model.live.order_management

import com.google.gson.annotations.SerializedName

data class OrderManagementPendingResponseBody(
    @SerializedName("OrderId")
    var orderId: Int = 0,
    @SerializedName("InsertedOn")
    var insertedOn: String? = "",
    @SerializedName("LiveProductId")
    var liveProductId: Int = 0,
    @SerializedName("ProductPrice")
    var productPrice: String? = "",
    @SerializedName("FolderName")
    var folderName: String? = "",
    @SerializedName("ProductTitle")
    var productTitle: String? = "",
    @SerializedName("DealQtn")
    var dealQtn: Int = 0,
    @SerializedName("ImageUrl")
    var imageUrl: String? = ""
)