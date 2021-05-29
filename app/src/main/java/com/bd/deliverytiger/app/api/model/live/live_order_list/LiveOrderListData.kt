package com.bd.deliverytiger.app.api.model.live.live_order_list

import com.google.gson.annotations.SerializedName

data class LiveOrderListData(
    @SerializedName("FolderName")
    var folderName: String = "",
    @SerializedName("ProductPrice")
    var productPrice: Int = 0,
    @SerializedName("OrderDate")
    var orderDate: String? = "",
    @SerializedName("ImageUrl")
    var imageUrl: String? = "",
    @SerializedName("OrderId")
    var orderId: Int = 0
)