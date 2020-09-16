package com.bd.deliverytiger.app.api.model.config


import com.google.gson.annotations.SerializedName

data class BannerModel(
    @SerializedName("showBanner")
    var showBanner: Boolean = false,
    @SerializedName("banners")
    var bannerData: List<BannerData> = listOf()
)