package com.bd.deliverytiger.app.api.model.config


import com.google.gson.annotations.SerializedName

data class BannerResponse(
    @SerializedName("popUp")
    var popUpModel: PopupModel = PopupModel(),
    @SerializedName("bannerList")
    var bannerModel: BannerModel = BannerModel(),

    @SerializedName("dashboardDataDuration")
    var dashboardDataDuration: Int = 12,
    @SerializedName("showOrderPopup")
    var showOrderPopup: Boolean = false,
    @SerializedName("instantPaymentOTPLimit")
    var instantPaymentOTPLimit: Int = 5000,
    @SerializedName("instantPaymentHourLimit")
    var instantPaymentHourLimit: Int = 12,
    @SerializedName("referBanner")
    var referBanner: String? = ""
)