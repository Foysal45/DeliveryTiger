package com.bd.deliverytiger.app.api.model.config


import com.google.gson.annotations.SerializedName

data class BannerResponse(
    @SerializedName("popUp")
    var popUpModel: PopupModel = PopupModel(),
    @SerializedName("bannerList")
    var bannerModel: BannerModel = BannerModel(),
    @SerializedName("dashboardDataDuration")
    var dashboardDataDuration: Int = 12
)