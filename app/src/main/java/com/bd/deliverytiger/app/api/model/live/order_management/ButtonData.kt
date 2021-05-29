package com.bd.deliverytiger.app.api.model.live.order_management

import com.google.gson.annotations.SerializedName

data class ButtonData(
    @SerializedName("ButtonName")
    var buttonName: String? = "",
    @SerializedName("Status")
    var status: Int = 0,
    @SerializedName("ActionUrl")
    var actionUrl: String? = "",
    @SerializedName("MethodType")
    var methodType: String? = "",
    @SerializedName("IsAlreadyPrint")
    var isAlreadyPrint: Int = 0
)