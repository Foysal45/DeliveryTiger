package com.bd.deliverytiger.app.api.model.live.live_product_insert

import com.google.gson.annotations.SerializedName

data class ProductGalleryData(
    @SerializedName("LiveId")
    var liveId: Int = 0,
    @SerializedName("ProductId")
    var productId: Int = 0
)