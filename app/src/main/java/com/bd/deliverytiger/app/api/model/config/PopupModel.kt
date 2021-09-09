package com.bd.deliverytiger.app.api.model.config


import com.google.gson.annotations.SerializedName

data class PopupModel(
    @SerializedName("popUpUrl")
    var popUpUrl: String? = "",
    @SerializedName("showPopUp")
    var showPopUp: Boolean = false,
    @SerializedName("popUpFrequency")
    var popUpFrequency: Int = 0,
    @SerializedName("route")
    var route: String? = ""
)