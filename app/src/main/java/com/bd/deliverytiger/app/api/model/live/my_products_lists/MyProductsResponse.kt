package com.bd.deliverytiger.app.api.model.live.my_products_lists

import com.google.gson.annotations.SerializedName

data class MyProductsResponse(
    @SerializedName("Id")
    var Id: Int = 0,
    @SerializedName("LiveId")
    var liveId: Int = 0,
    @SerializedName("IsActive")
    var isActive: Int = 0,
    @SerializedName("IsSoldOut")
    var isSoldOut: Boolean = false,
    @SerializedName("ProductTitle")
    var productTitle: String = "",
    @SerializedName("ProductPrice")
    var ProductPrice: Int = 0,
    @SerializedName("DiscountPercentage")
    var discountPercentage: Int = 0,
    @SerializedName("DiscountAfterPrice")
    var discountAfterPrice: Int = 0,
    @SerializedName("InsertedBy")
    var insertedBy: Int = 0,
    @SerializedName("CoverPhoto")
    var coverPhoto: String = "",
    @SerializedName("FolderName")
    var FolderName: String = "",
    @SerializedName("MerchantId")
    var merchantId: Int = 0,
    @SerializedName("DealQtn")
    var dealQtn: Int = 0
)
