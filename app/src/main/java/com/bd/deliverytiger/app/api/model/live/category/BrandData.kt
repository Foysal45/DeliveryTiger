package com.bd.deliverytiger.app.api.model.live.category


import com.google.gson.annotations.SerializedName

data class BrandData(
    @SerializedName("BrandId")
    var brandId: Int? = 0,
    @SerializedName("BrandName")
    var brandName: String? = "",
    @SerializedName("BrandNameInEnglish")
    var brandNameInEnglish: String? = "",
    @SerializedName("BrandImage")
    var brandImage: String? = ""
)