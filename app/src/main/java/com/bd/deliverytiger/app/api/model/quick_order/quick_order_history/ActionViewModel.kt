package com.bd.deliverytiger.app.api.model.quick_order.quick_order_history


import com.google.gson.annotations.SerializedName

data class ActionViewModel(
    @SerializedName("buttonName")
    var buttonName: String? = "",
    @SerializedName("statusUpdate")
    var statusUpdate: Int = 0,
    @SerializedName("statusMessage")
    var statusMessage: String? = "",
    @SerializedName("colorCode")
    var colorCode: String? = ""
)