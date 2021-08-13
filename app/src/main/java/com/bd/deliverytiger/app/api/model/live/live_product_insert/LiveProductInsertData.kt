package com.bd.deliverytiger.app.api.model.live.live_product_insert

import com.google.gson.annotations.SerializedName

data class LiveProductInsertData(

    @SerializedName("ProductPrice")
    var productPrice: Int = 0,
    @SerializedName("DiscountAfterPrice")
    var discountAfterPrice: Int = 0,
    @SerializedName("ProductTitle")
    var productTitle: String = "",
    var imageLink: String = "",
    @SerializedName("InsertedBy")
    var insertedBy: Int = 0,
    @SerializedName("LiveId")
    var liveId: Int = 0,
    @SerializedName("IsActive")
    var isActive: Int = 1,
    @SerializedName("DiscountPercentage")
    var discountPercentage: Int = 0,
    @SerializedName("DealQtn")
    var dealQtn: Int = 0

)