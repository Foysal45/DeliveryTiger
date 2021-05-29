package com.bd.deliverytiger.app.api.model.live.brand

import com.google.gson.annotations.SerializedName

data class BrandData (
    @SerializedName("BrandId")
    var brandId: Int,
    @SerializedName("BrandName")
    var brandName: String,
    @SerializedName("BrandNameEng")
    var brandNameEng: String
)