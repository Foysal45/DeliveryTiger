package com.bd.deliverytiger.app.api.model.config


import com.google.gson.annotations.SerializedName

data class BannerConfig(
    @SerializedName("showBanner")
    var showBanner: Boolean = false,
    @SerializedName("showPopupBanner")
    var showPopupBanner: Boolean = false,
    @SerializedName("bannerUrl")
    var bannerUrl: String? = "",
    @SerializedName("popupBannerUrl")
    var popupBannerUrl: String? = "",
    @SerializedName("popupFrequency")
    var popupFrequency: Int = 0
)