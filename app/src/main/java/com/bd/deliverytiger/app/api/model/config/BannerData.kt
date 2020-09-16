package com.bd.deliverytiger.app.api.model.config


import com.google.gson.annotations.SerializedName

data class BannerData(
    @SerializedName("bannerUrl")
    var bannerUrl: String? = "",
    @SerializedName("isActive")
    var isActive: Boolean = false
)