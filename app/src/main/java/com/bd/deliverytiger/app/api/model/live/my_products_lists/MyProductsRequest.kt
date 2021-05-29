package com.bd.deliverytiger.app.api.model.live.my_products_lists

import com.google.gson.annotations.SerializedName

data class MyProductsRequest(
    @SerializedName("Index")
    var index: Int = 0,
    @SerializedName("Count")
    var count: Int = 0,
    @SerializedName("MerchantId")
    var merchantId: Int = 0
)
