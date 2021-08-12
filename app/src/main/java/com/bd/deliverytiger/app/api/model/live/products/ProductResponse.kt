package com.bd.deliverytiger.app.api.model.live.products

import com.google.gson.annotations.SerializedName

data class ProductResponse(
    @SerializedName("TotalCount")
    var totalCount: Int = 0,
    @SerializedName("FreeProducts")
    var productList: List<ProductData> = listOf()
)